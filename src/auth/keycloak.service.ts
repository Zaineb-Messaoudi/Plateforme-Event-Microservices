import { Injectable } from '@nestjs/common';
import axios from 'axios';

@Injectable()
export class KeycloakService {
  private baseUrl = process.env.KEYCLOAK_URL;
  private realm = process.env.KEYCLOAK_REALM;

  // =========================
  // TOKEN URL
  // =========================
  private get tokenUrl() {
    return `${this.baseUrl}/realms/${this.realm}/protocol/openid-connect/token`;
  }

  // =========================
  // ADMIN TOKEN
  // =========================
  async getAdminToken() {
    const params = new URLSearchParams({
      grant_type: 'password',
      client_id: 'admin-cli',
      username: process.env.KEYCLOAK_ADMIN_USER!,
      password: process.env.KEYCLOAK_ADMIN_PASSWORD!,
    });

    const { data } = await axios.post(
      `${this.baseUrl}/realms/master/protocol/openid-connect/token`,
      params,
    );

    return data.access_token;
  }

  // =========================
  // REGISTER USER
  // =========================
  async register(dto: any) {
    const token = await this.getAdminToken();

    await axios.post(
      `${this.baseUrl}/admin/realms/${this.realm}/users`,
      {
        username: dto.email,
        email: dto.email,
        firstName: dto.firstName,
        lastName: dto.lastName,
        enabled: true,
        credentials: [
          {
            type: 'password',
            value: dto.password,
            temporary: false,
          },
        ],
      },
      {
        headers: { Authorization: `Bearer ${token}` },
      },
    );
  }

  // =========================
  // LOGIN
  // =========================
  async login(email: string, password: string) {
    const params = new URLSearchParams({
      grant_type: 'password',
      client_id: process.env.KEYCLOAK_CLIENT_ID!,
      client_secret: process.env.KEYCLOAK_CLIENT_SECRET!,
      username: email,
      password,
    });

    const { data } = await axios.post(this.tokenUrl, params);

    return data;
  }

  // =========================
  // LOGOUT
  // =========================
  async logout(refreshToken: string) {
    const params = new URLSearchParams({
      client_id: process.env.KEYCLOAK_CLIENT_ID!,
      client_secret: process.env.KEYCLOAK_CLIENT_SECRET!,
      refresh_token: refreshToken,
    });

    await axios.post(
      `${this.baseUrl}/realms/${this.realm}/protocol/openid-connect/logout`,
      params,
    );
  }

  // =========================
  // REFRESH TOKEN
  // =========================
  async refreshToken(refreshToken: string) {
    const params = new URLSearchParams({
      grant_type: 'refresh_token',
      client_id: process.env.KEYCLOAK_CLIENT_ID!,
      client_secret: process.env.KEYCLOAK_CLIENT_SECRET!,
      refresh_token: refreshToken,
    });

    const { data } = await axios.post(this.tokenUrl, params);
    return data;
  }

  // =========================
  // ASSIGN ROLE
  // =========================
  async assignRole(userId: string, role: string) {
    const token = await this.getAdminToken();

    const { data: roles } = await axios.get(
      `${this.baseUrl}/admin/realms/${this.realm}/roles/${role}`,
      {
        headers: { Authorization: `Bearer ${token}` },
      },
    );

    await axios.post(
      `${this.baseUrl}/admin/realms/${this.realm}/users/${userId}/role-mappings/realm`,
      [roles],
      {
        headers: { Authorization: `Bearer ${token}` },
      },
    );
  }
}