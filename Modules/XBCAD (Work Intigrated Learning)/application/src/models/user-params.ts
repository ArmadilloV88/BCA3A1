import { SkillLevel } from './skill-level'
import { UserRole } from './user-role'

export interface UserParams {
  userId?: string
  firstName: string
  lastName: string

  emailAddress: string[]
  phoneNumber: string

  skillLevelId: number
  skillLevel: SkillLevel

  userRoleId: number
  userRole: UserRole

  password?: string
}
