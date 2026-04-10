// src/auth/auth.controller.ts
import {
  Controller, Post, Body, Req, UseGuards,
  HttpCode, HttpStatus, Get, Patch
} from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';
import { ApiTags, ApiBearerAuth, ApiOperation } from '@nestjs/swagger';
import { KeycloakService } from './keycloak.service';
import { UsersService } from 'src/user/user.service';
import { RegisterDto } from './dto/register.dto';
import { LoginDto } from './dto/login.dto';
import { ForgotPasswordDto } from './dto/forgot-password.dto';
import { ResetPasswordDto } from './dto/reset-password.dto';
import { RefreshTokenDto } from './dto/refresh-token.dto';

@ApiTags('auth')
@Controller('auth')
export class AuthController {
  constructor(
    private readonly keycloakService: KeycloakService,
    private readonly usersService: UsersService,
  ) {}

  @Post('register')
  @ApiOperation({ summary: 'Register a new user or organisateur' })
  async register(@Body() dto: RegisterDto) {
    // 1. Create in Keycloak (sends verification email)
    const keycloakUser = await this.keycloakService.register(dto);

    // 2. Create profile in MongoDB
    const user = await this.usersService.findOrCreate(keycloakUser.id, {
      email: dto.email,
      firstName: dto.firstName,
      lastName: dto.lastName,
      role: dto.role,
    });

    return {
      message: 'Registration successful. Please check your email to verify your account.',
      user,
    };
  }

  @Post('login')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Login and receive JWT tokens' })
  async login(@Body() dto: LoginDto) {
    const tokens = await this.keycloakService.login(dto.email, dto.password);

    // Upsert profile (handles first login edge case)
    const userInfo = await this.keycloakService.getUserInfo(tokens.accessToken);
    await this.usersService.findOrCreate(userInfo.sub, {
      email: userInfo.email,
      firstName: userInfo.given_name,
      lastName: userInfo.family_name,
    });

    return tokens;
  }

  @Post('logout')
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiOperation({ summary: 'Logout and invalidate refresh token' })
  async logout(@Body() dto: RefreshTokenDto) {
    await this.keycloakService.logout(dto.refreshToken);
  }

  @Post('refresh')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Get a new access token using refresh token' })
  async refresh(@Body() dto: RefreshTokenDto) {
    return this.keycloakService.refreshToken(dto.refreshToken);
  }

  @Post('forgot-password')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Send password reset email' })
  async forgotPassword(@Body() dto: ForgotPasswordDto) {
    return this.keycloakService.forgotPassword(dto.email);
  }

  @Post('resend-verification')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Resend email verification link' })
  async resendVerification(@Body() dto: ForgotPasswordDto) {
    // Reuses forgot-password email lookup pattern
    const adminToken = await this.keycloakService['getAdminToken']();
    const { data: users } = await require('axios').get(
      `${this.keycloakService['adminUrl']}/users?email=${dto.email}`,
      { headers: { Authorization: `Bearer ${adminToken}` } }
    );
    if (users.length) {
      await this.keycloakService.sendVerificationEmail(users[0].id);
    }
    return { message: 'If this email exists and is unverified, a new link has been sent' };
  }

  // ─── Protected routes (require valid JWT) ───────────────────────────────

  @Patch('reset-password')
  @UseGuards(AuthGuard('jwt'))
  @ApiBearerAuth()
  @ApiOperation({ summary: 'Change password while logged in' })
  async resetPassword(@Req() req: any, @Body() dto: ResetPasswordDto) {
    await this.keycloakService.resetPassword(req.user.keycloakId, dto.newPassword);
    return { message: 'Password updated successfully' };
  }

  @Get('me')
  @UseGuards(AuthGuard('jwt'))
  @ApiBearerAuth()
  @ApiOperation({ summary: 'Get current user profile' })
  async getMe(@Req() req: any) {
    return this.usersService.findOrCreate(req.user.keycloakId, {
      email: req.user.email,
      firstName: req.user.firstName,
      lastName: req.user.lastName,
    });
  }
}