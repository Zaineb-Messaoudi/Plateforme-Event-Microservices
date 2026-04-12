import { Controller, Post, Body } from '@nestjs/common';
import { ApiTags, ApiBody } from '@nestjs/swagger';
import { KeycloakService } from './keycloak.service';

@ApiTags('auth')
@Controller('auth')
export class AuthController {
  constructor(private kc: KeycloakService) {}

  // REGISTER
  @Post('register')
  @ApiBody({
    schema: {
      example: {
        email: 'test@gmail.com',
        password: '12345678',
        firstName: 'John',
        lastName: 'Doe',
        role: 'UTILISATEUR',
      },
    },
  })
  async register(@Body() dto: any) {
    await this.kc.register(dto);
    return { message: 'User created' };
  }

  // LOGIN
  @Post('login')
  async login(@Body() dto: any) {
    return this.kc.login(dto.email, dto.password);
  }

  // LOGOUT
  @Post('logout')
  async logout(@Body() dto: any) {
    await this.kc.logout(dto.refreshToken);
    return { message: 'Logged out' };
  }

  // REFRESH
  @Post('refresh')
  async refresh(@Body() dto: any) {
    return this.kc.refreshToken(dto.refreshToken);
  }
}