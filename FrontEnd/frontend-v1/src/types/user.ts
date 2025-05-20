// src/types/user.ts
export interface Role {
    id?: number;
    name: "ADMIN" | "USER";
  }
  
export interface User {
    id: number;
    username: string;
    email: string;
    password?: string | null;
    fullName: string;
    bio?: string;
    profilePicture?: string | null;
    isVerified: boolean;
    status: "ACTIVE" | "SUSPENDED" | "DELETED" | "PENDING_VERIFICATION";
    createdAt: string;
    updatedAt: string;
    roles: Role[];
  }