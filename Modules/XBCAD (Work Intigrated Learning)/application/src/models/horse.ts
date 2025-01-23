import { SkillLevel } from './skill-level'
import { User } from './user-params'

export interface Horse {
  id: string
  name: string
  age: number
  breed: string
  yearsWorked: number
  createdBy: number
  user?: User

  skillLevelId?: number
  skillLevel?: SkillLevel
  // skillLevel: 'Beginner' | 'Intermediate' | 'Advanced'
}
