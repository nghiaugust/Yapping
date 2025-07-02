// src/utils/constants.ts
export const API_BASE_URL = 'http://localhost:8080/yapping';

/**
 * Utility function to get full URL for media files
 * @param path - relative path to the media file or just filename
 * @returns full URL with domain
 */
export const getMediaUrl = (path: string | null | undefined): string | undefined => {
  if (!path) return undefined;
  
  // If path already contains the full path, use it directly
  if (path.startsWith('/uploads/media/') || path.startsWith('uploads/media/')) {
    return `${API_BASE_URL}${path.startsWith('/') ? path : '/' + path}`;
  }
  
  // If it's just a filename, prepend the media path
  const filename = path.split('/').pop(); // Extract filename from any path
  return `${API_BASE_URL}/uploads/media/${filename}`;
};

/**
 * Utility function to get full URL for profile pictures
 * @param path - relative path to the profile picture or just filename
 * @returns full URL with domain
 */
export const getProfilePictureUrl = (path: string | null | undefined): string | undefined => {
  if (!path) return undefined;
  
  // If path already contains the full path, use it directly
  if (path.startsWith('/uploads/profile-pictures/') || path.startsWith('uploads/profile-pictures/')) {
    return `${API_BASE_URL}${path.startsWith('/') ? path : '/' + path}`;
  }
  
  // If it's just a filename, prepend the profile pictures path
  const filename = path.split('/').pop(); // Extract filename from any path
  return `${API_BASE_URL}/uploads/profile-pictures/${filename}`;
};
