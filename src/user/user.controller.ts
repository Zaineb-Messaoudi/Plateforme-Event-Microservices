// src/users/users.controller.ts
import { Controller, Get, Patch, Param, Body, UseGuards, Req } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';
import { UsersService } from './user.service';
import { RolesGuard } from 'src/auth/guards/roles.guards';
import { Roles } from 'src/auth/decorators/roles.decorators';
import { UserRole } from './shema/user.schema';
import { ApiTags, ApiBearerAuth } from '@nestjs/swagger';
import { UpdateProfileDto } from 'src/auth/dto/update-profile.dto';
import { UserStatus } from './enums/user-status.enum';

@ApiTags('users')
@ApiBearerAuth()
@UseGuards(AuthGuard('jwt'), RolesGuard)
@Controller('users')
export class UsersController {
  constructor(private readonly usersService: UsersService) {}

  @Get('me')
  async getMe(@Req() req: any) {
    return this.usersService.findOrCreate(req.user.keycloakId, {
      email: req.user.email,
      firstName: req.user.firstName,
      lastName: req.user.lastName,
    });
  }

  @Patch('me')
  async updateMe(@Req() req: any, @Body() body: UpdateProfileDto) {
    return this.usersService.updateProfile(req.user.keycloakId, body);
  }

  @Get()
  @Roles(UserRole.ADMIN)
  async findAll() {
    return this.usersService.findAll();
  }

  @Get(':id')
  @Roles(UserRole.ADMIN)
  async findOne(@Param('id') id: string) {
    return this.usersService.findById(id);
  }

  @Patch(':id/role')
  @Roles(UserRole.ADMIN)
  async updateRole(@Param('id') id: string, @Body('role') role: UserRole) {
    return this.usersService.updateRole(id, role);
  }

  @Patch(':id/status')
  @Roles(UserRole.ADMIN)
  async updateStatus(@Param('id') id: string, @Body('status') status:UserStatus) {
    return this.usersService.updateStatus(id, status);
  }

  @Get('organisateurs')
  @Roles(UserRole.ADMIN, UserRole.ORGANISATEUR)
  async findOrganisateurs() {
    return this.usersService.findAll(UserRole.ORGANISATEUR);
  }
}