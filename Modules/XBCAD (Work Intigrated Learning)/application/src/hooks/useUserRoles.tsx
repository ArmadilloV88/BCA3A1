import axios from '@/lib/axios'
import { SkillLevel } from '@/models/skill-level'
import { UserRole } from '@/models/user-role'
import { useQuery } from '@tanstack/react-query'

export default function useUserRoles() {
  const userRoles = useQuery({
    queryKey: ['user-roles'],
    queryFn: async () => {
      const { data } = await axios.get('/userRole')
      return data as UserRole[]
    },
  })

  return {
    userRoles,
  }
}
