// src/types/mention.ts
// Đồng bộ với Mention.java

export interface Mention {
  id: number;
  postId?: number;
  commentId?: number;
  mentionerId: number; // Người mention
  mentionedId: number; // Người được mention
  createdAt: string;
}

export interface MentionWithDetails extends Mention {
  mentioner: {
    id: number;
    username: string;
    fullName: string;
    profilePicture?: string;
  };
  mentioned: {
    id: number;
    username: string;
    fullName: string;
    profilePicture?: string;
  };
  post?: {
    id: number;
    content: string;
  };
  comment?: {
    id: number;
    content: string;
  };
}

export interface CreateMentionRequest {
  postId?: number;
  commentId?: number;
  mentionedUsernames: string[]; // Danh sách username được mention
}
