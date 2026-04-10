// src/auth/keycloak.service.ts
import { Injectable, UnauthorizedException, BadRequestException, InternalServerErrorException } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import axios, { AxiosError } from 'axios';

@Injectable()
export class KeycloakService {
  private readonly baseUrl: string;
  private readonly realm: string;
  private readonly clientId: string;
  private readonly clientSecret: string;

  constructor(private config: ConfigService) {
    this.baseUrl = config.get('KEYCLOAK_URL')!;
    this.realm = config.get('KEYCLOAK_REALM')!;
    this.clientId = config.get('KEYCLOAK_CLIENT_ID')!;
    this.clientSecret = config.get('KEYCLOAK_CLIENT_SECRET')!;
  }

  // ─── Token endpoint base ───────────────────────────────────────────────────

  private get tokenUrl() {
    return `${this.baseUrl}/realms/${this.realm}/protocol/openid-connect/token`;
  }

  private get adminUrl() {
    return `${this.baseUrl}/admin/realms/${this.realm}`;
  }

  // ─── Get admin token (for Admin API calls) ─────────────────────────────────

  private async getAdminToken(): Promise<string> {
   const params = new URLSearchParams({
  grant_type: 'password',
  client_id: 'admin-cli',
  username: this.config.get('KEYCLOAK_ADMIN_USER')!,
  password: this.config.get('KEYCLOAK_ADMIN_PASSWORD')!,
});

    try {
      const { data } = await axios.post(
        `${this.baseUrl}/realms/master/protocol/openid-connect/token`,
        params,
        { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }
      );
      return data.access_token;
    } catch {
      throw new InternalServerErrorException('Failed to get admin token');
    }
  }

  // ─── SIGNUP ────────────────────────────────────────────────────────────────

  async register(dto: {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    role: 'UTILISATEUR' | 'ORGANISATEUR';
  }) {
    const adminToken = await this.getAdminToken();

    // 1. Create user in Keycloak
    try {
      await axios.post(
        `${this.adminUrl}/users`,
        {
          email: dto.email,
          firstName: dto.firstName,
          lastName: dto.lastName,
          enabled: true,
          emailVerified: false,
          credentials: [{ type: 'password', value: dto.password, temporary: false }],
        },
        { headers: { Authorization: `Bearer ${adminToken}` } }
      );
    } catch (err: any) {
      if (err.response?.status === 409) {
        throw new BadRequestException('Email already registered');
      }
      throw new InternalServerErrorException('Failed to create user');
    }

    // 2. Get the new user's Keycloak ID
    const { data: users } = await axios.get(`${this.adminUrl}/users?email=${dto.email}`, {
      headers: { Authorization: `Bearer ${adminToken}` },
    });
    const keycloakUser = users[0];

    // 3. Assign realm role
    await this.assignRole(keycloakUser.id, dto.role, adminToken);

    // 4. Send verification email
    await this.sendVerificationEmail(keycloakUser.id, adminToken);

    return keycloakUser;
  }

  // ─── LOGIN ─────────────────────────────────────────────────────────────────

  async login(email: string, password: string) {
    const params = new URLSearchParams({
      grant_type: 'password',
      client_id: this.clientId,
      client_secret: this.clientSecret,
      username: email,
      password,
      scope: 'openid profile email roles',
    });

    try {
      const { data } = await axios.post(this.tokenUrl, params, {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      });
      return {
        accessToken: data.access_token,
        refreshToken: data.refresh_token,
        expiresIn: data.expires_in,
        tokenType: data.token_type,
      };
    } catch (err: any) {
      const status = err.response?.status;
      if (status === 401) throw new UnauthorizedException('Invalid email or password');
      if (status === 400 && err.response?.data?.error === 'account_disabled') {
        throw new UnauthorizedException('Account is disabled');
      }
      throw new UnauthorizedException('Login failed');
    }
  }

  // ─── LOGOUT ────────────────────────────────────────────────────────────────

  async logout(refreshToken: string) {
    const params = new URLSearchParams({
      client_id: this.clientId,
      client_secret: this.clientSecret,
      refresh_token: refreshToken,
    });

    await axios.post(
      `${this.baseUrl}/realms/${this.realm}/protocol/openid-connect/logout`,
      params,
      { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }
    );
    // Keycloak invalidates the refresh token — the access token expires naturally
  }

  // ─── REFRESH TOKEN ─────────────────────────────────────────────────────────

  async refreshToken(refreshToken: string) {
    const params = new URLSearchParams({
      grant_type: 'refresh_token',
      client_id: this.clientId,
      client_secret: this.clientSecret,
      refresh_token: refreshToken,
    });

    try {
      const { data } = await axios.post(this.tokenUrl, params, {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      });
      return {
        accessToken: data.access_token,
        refreshToken: data.refresh_token,
        expiresIn: data.expires_in,
      };
    } catch {
      throw new UnauthorizedException('Session expired, please login again');
    }
  }

  // ─── FORGOT PASSWORD ───────────────────────────────────────────────────────
  // Keycloak sends the reset email automatically

  async forgotPassword(email: string) {
    const adminToken = await this.getAdminToken();

    const { data: users } = await axios.get(
      `${this.adminUrl}/users?email=${encodeURIComponent(email)}`,
      { headers: { Authorization: `Bearer ${adminToken}` } }
    );

    if (!users.length) {
      // Don't reveal whether email exists — security best practice
      return { message: 'If this email exists, a reset link has been sent' };
    }

    await axios.put(
      `${this.adminUrl}/users/${users[0].id}/execute-actions-email`,
      ['UPDATE_PASSWORD'],
      { headers: { Authorization: `Bearer ${adminToken}`, 'Content-Type': 'application/json' } }
    );

    return { message: 'If this email exists, a reset link has been sent' };
  }

  // ─── RESET PASSWORD (authenticated) ───────────────────────────────────────
  // Used when user is logged in and wants to change their password

  async resetPassword(keycloakId: string, newPassword: string) {
    const adminToken = await this.getAdminToken();

    await axios.put(
      `${this.adminUrl}/users/${keycloakId}/reset-password`,
      { type: 'password', value: newPassword, temporary: false },
      { headers: { Authorization: `Bearer ${adminToken}`, 'Content-Type': 'application/json' } }
    );
  }

  // ─── VERIFY EMAIL ──────────────────────────────────────────────────────────
  // Resend verification email

  async sendVerificationEmail(keycloakId: string, adminToken?: string) {
    const token = adminToken ?? await this.getAdminToken();

    await axios.put(
      `${this.adminUrl}/users/${keycloakId}/execute-actions-email`,
      ['VERIFY_EMAIL'],
      { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } }
    );
  }

  // ─── UPDATE EMAIL ──────────────────────────────────────────────────────────

  async updateEmail(keycloakId: string, newEmail: string) {
    const adminToken = await this.getAdminToken();

    await axios.put(
      `${this.adminUrl}/users/${keycloakId}`,
      { email: newEmail, emailVerified: false },
      { headers: { Authorization: `Bearer ${adminToken}`, 'Content-Type': 'application/json' } }
    );

    // Trigger re-verification
    await this.sendVerificationEmail(keycloakId, adminToken);
  }

  // ─── DISABLE / ENABLE ACCOUNT (Admin) ─────────────────────────────────────

  async setUserEnabled(keycloakId: string, enabled: boolean) {
    const adminToken = await this.getAdminToken();

    await axios.put(
      `${this.adminUrl}/users/${keycloakId}`,
      { enabled },
      { headers: { Authorization: `Bearer ${adminToken}`, 'Content-Type': 'application/json' } }
    );
  }

  // ─── ASSIGN ROLE ───────────────────────────────────────────────────────────

  private async assignRole(keycloakId: string, roleName: string, adminToken: string) {
    const { data: role } = await axios.get(
      `${this.adminUrl}/roles/${roleName}`,
      { headers: { Authorization: `Bearer ${adminToken}` } }
    );

    await axios.post(
      `${this.adminUrl}/users/${keycloakId}/role-mappings/realm`,
      [role],
      { headers: { Authorization: `Bearer ${adminToken}`, 'Content-Type': 'application/json' } }
    );
  }

  // ─── GET USER INFO from access token ──────────────────────────────────────

  async getUserInfo(accessToken: string) {
    const { data } = await axios.get(
      `${this.baseUrl}/realms/${this.realm}/protocol/openid-connect/userinfo`,
      { headers: { Authorization: `Bearer ${accessToken}` } }
    );
    return data;
  }
}