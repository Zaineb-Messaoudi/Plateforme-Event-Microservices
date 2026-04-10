// src/users/schemas/user.schema.ts
import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { Document } from 'mongoose';

export type UserDocument = User & Document;

export enum UserRole {
  ADMIN = 'ADMIN',
  ORGANISATEUR = 'ORGANISATEUR',
  UTILISATEUR = 'UTILISATEUR',
}

export enum UserStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED',
}

@Schema({ timestamps: true })
export class User {
  @Prop({ required: true, unique: true })
  keycloakId: string;   // links to Keycloak user ID

  @Prop({ required: true, unique: true })
  email: string;

  @Prop({ required: true })
  firstName: string;

  @Prop({ required: true })
  lastName: string;

  @Prop({ type: String, enum: UserRole, default: UserRole.UTILISATEUR })
  role: UserRole;

  @Prop({ type: String, enum: UserStatus, default: UserStatus.ACTIVE })
  status: UserStatus;

  // Organisateur-specific fields
  @Prop()
  organizationName?: string;

  @Prop()
  bio?: string;

  // Subscription info (referenced by Subscription microservice)
  @Prop({ default: false })
  hasPremium: boolean;

  // Preferences
  @Prop({ type: [String], default: [] })
  preferredCategories: string[];
}

export const UserSchema = SchemaFactory.createForClass(User);