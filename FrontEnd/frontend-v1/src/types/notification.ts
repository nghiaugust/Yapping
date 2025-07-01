// src/types/notification.ts

export interface NotificationDTO {
  id: number;
  userId: number;
  username: string;
  userFullName: string;
  userProfilePicture: string;
  actorId?: number;
  actorUsername?: string;
  actorFullName?: string;
  actorProfilePicture?: string;
  type: NotificationType;
  targetType: TargetType;
  targetId?: number;
  targetOwnerId?: number;
  targetOwnerUsername?: string;
  targetOwnerFullName?: string;
  isRead: boolean;
  createdAt: string;
  message: string;
  redirectUrl: string;
}

export enum NotificationType {
  LIKE_POST = "LIKE_POST",
  LIKE_COMMENT = "LIKE_COMMENT",
  COMMENT = "COMMENT",
  REPLY_POST = "REPLY_POST",
  REPLY_COMMENT = "REPLY_COMMENT",
  FOLLOW = "FOLLOW",
  MENTION_POST = "MENTION_POST",
  MENTION_COMMENT = "MENTION_COMMENT",
  REPOST = "REPOST",
  SYSTEM = "SYSTEM",
  NEW_POST = "NEW_POST"
}

export enum TargetType {
  POST = "POST",
  COMMENT = "COMMENT",
  USER = "USER"
}

export interface NotificationPageResponse {
  content: NotificationDTO[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      orders: {
        direction: string;
        property: string;
      }[];
    };
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  size: number;
  number: number;
  sort: {
    orders: {
      direction: string;
      property: string;
    }[];
  };
  numberOfElements: number;
  empty: boolean;
}

export interface MarkNotificationsReadDTO {
  notificationIds?: number[];
  allNotifications?: boolean;
}

export interface CreateNotificationDTO {
  userId: number;
  actorId?: number;
  type: NotificationType;
  targetType: TargetType;
  targetId?: number;
  targetOwnerId?: number;
  message?: string;
}
