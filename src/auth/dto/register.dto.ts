import { ApiProperty } from '@nestjs/swagger';
import { IsEmail, IsString } from 'class-validator';

export class RegisterDto {
  @ApiProperty({ example: 'test@gmail.com' })
  @IsEmail()
  email !: string;

  @ApiProperty({ example: '12345678' })
  @IsString()
  password !: string;

  @ApiProperty({ example: 'John' })
  @IsString()
  firstName !: string;

  @ApiProperty({ example: 'Doe' })
  @IsString()
  lastName !: string;

  @ApiProperty({ example: 'UTILISATEUR' })
  role !: 'UTILISATEUR' | 'ORGANISATEUR';
}