// src/users/users.service.ts
import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { User,UserRole,UserDocument,UserStatus } from './shema/user.schema';
import { UpdateProfileDto } from 'src/auth/dto/update-profile.dto';
import { CreateUserDto } from 'src/auth/dto/create-user.dto';

@Injectable()
export class UsersService {
  constructor(@InjectModel(User.name) private userModel: Model<UserDocument>) {}

  async findOrCreate(keycloakId: string, data: Partial<User>): Promise<UserDocument> {
    const existing = await this.userModel.findOne({ keycloakId });
    if (existing) return existing;
    return this.userModel.create({ keycloakId, ...data });
  }

  async findById(keycloakId: string): Promise<UserDocument> {
    const user = await this.userModel.findOne({ keycloakId });
    if (!user) throw new NotFoundException('User not found');
    return user;
  }

  async findAll(role?: UserRole): Promise<UserDocument[]> {
    const filter = role ? { role } : {};
    return this.userModel.find(filter).exec();
  }

  async updateRole(keycloakId: string, role: UserRole): Promise<UserDocument> {
    const user = await this.userModel.findOneAndUpdate(
      { keycloakId },
      { role },
      { new: true },
    );
    if (!user) throw new NotFoundException('User not found');
    return user;
  }

  async updateStatus(keycloakId: string, status: UserStatus): Promise<UserDocument> {
    const user = await this.userModel.findOneAndUpdate(
      { keycloakId },
      { status },
      { new: true },
    );
    if (!user) throw new NotFoundException('User not found');
    return user;
  }

  // ✅ Nouvelle méthode updateProfile
  async updateProfile(keycloakId: string, data: UpdateProfileDto): Promise<UserDocument> {
    const user = await this.userModel.findOneAndUpdate(
      { keycloakId },
      data,
      { new: true },
    );
    if (!user) throw new NotFoundException('User not found');
    return user;
  }
}