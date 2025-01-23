import axios from '@/lib/axios'
import { SkillLevel } from '@/models/skill-level'
import { useQuery } from '@tanstack/react-query'

export default function useSkillLevels() {
  const skillLevels = useQuery({
    queryKey: ['skill-levels'],
    queryFn: async () => {
      const { data } = await axios.get('/skillLevel')
      return data as SkillLevel[]
    },
  })

  return {
    skillLevels,
  }
}
