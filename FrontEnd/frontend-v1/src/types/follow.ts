// src/types/follow.ts
export interface Follow {
  followerId: number;
  followedId: number;
}

export interface FollowUser {
  id: number;
  username: string;
  fullName: string;
  profilePicture: string | null;
  timestamp?: string;
}

export interface ApiResponse<T> {
  status: number;
  success: boolean;
  message: string;
  data: T;
}
