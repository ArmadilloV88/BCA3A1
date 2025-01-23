import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import axios from '@/lib/axios'
import { Horse } from '@/models/horse'
import { toast } from 'sonner'

export default function useHorses() {
  const queryClient = useQueryClient()

  const horses = useQuery({
    queryKey: ['horses'],
    queryFn: async () => {
      const { data } = await axios.get('/horse?select=*,skillLevel(*),user(*)')
      return data as Horse[]
    },
    refetchInterval: 1000 * 60 * 2, // refetch every 2 mins
    refetchIntervalInBackground: false,
  })

  const horseCreation = useMutation({
    mutationKey: ['create-horse'],
    mutationFn: async (newHorse: Horse) => {
      await axios.post('/horse', newHorse)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['horses'] })

      horseCreation.reset()

      toast.success('Successfully Added Horse')
    },
    onError: (error) => {
      toast.error(`An error occurred: ${error.message}`)
    },
  })

  const horseUpdation = useMutation({
    mutationKey: ['update-horse'],
    mutationFn: async (horseToUpdate: Horse) => {
      await axios.patch(`/horse?id=eq.${horseToUpdate.id}`, horseToUpdate)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['horses'] })

      horseUpdation.reset()

      toast.success('Successfully Updated Horse')
    },
    onError: (error) => {
      toast.error(`An error occurred: ${error.message}`)
    },
  })

  const horseDeletion = useMutation({
    mutationKey: ['delete-horse'],
    mutationFn: async (horseIdToDelete: string) => {
      await axios.delete(`/user?id=eq.${horseIdToDelete}`)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['horses'] })

      horseDeletion.reset()

      toast.success('Successfully Deleted Horse')
    },
    onError: (error) => {
      toast.error(`An error occurred: ${error.message}`)
    },
  })

  return {
    horses,
    horseCreation,
    horseUpdation,
    horseDeletion,
  }
}
