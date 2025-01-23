import { SkillLevel } from './skill-level'
import { UserRole } from './user-role'

// The following code was adapted from youtube.com
// Author: Web Dev Simplified (https://www.youtube.com/@WebDevSimplified)
// Link: https://www.youtube.com/watch?v=5GG-VUvruzE&t=1631s
declare global {
  interface UserPublicMetadata {
    skillLevel: SkillLevel
    userRole: UserRole
    phoneNumber: string
  }
}
