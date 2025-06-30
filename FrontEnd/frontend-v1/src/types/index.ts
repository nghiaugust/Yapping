// src/types/index.ts
// Central export file for all TypeScript types

// Core types
export * from './user';
export * from './post';
export * from './comment';
export * from './like';
export * from './follow';
export * from './notification';

// Additional types based on backend
export * from './category';
export * from './resource';
export * from './repost';
export * from './mention';
export * from './report';
export * from './bookmark';

// Common API Response type - re-export from user.ts to avoid duplication
export type { ApiResponse, PageResponse } from './user';
