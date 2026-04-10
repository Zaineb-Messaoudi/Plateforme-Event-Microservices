// src/auth/dto/register.dto.ts
import { IsEmail, IsString, MinLength, IsEnum } from 'class-validator';
import { UserRole } from 'src/user/shema/user.schema';

export class RegisterDto {
  @IsEmail()
  email: string;

  @IsString()
  @MinLength(8)
  password: string;

  @IsString()
  firstName: string;

  @IsString()
  lastName: string;

  @IsEnum([UserRole.UTILISATEUR, UserRole.ORGANISATEUR])
  role: UserRole.UTILISATEUR | UserRole.ORGANISATEUR;
  // ADMIN role is never self-assigned — only promoted by another admin
}