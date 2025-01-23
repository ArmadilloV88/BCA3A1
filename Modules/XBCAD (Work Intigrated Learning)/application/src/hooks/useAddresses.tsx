import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import axios from '@/lib/axios'
import { Address } from '@/models/address'
import { toast } from 'sonner'

export default function useAddresses() {
  const queryClient = useQueryClient()

  const addresses = useQuery({
    queryKey: ['addresses'],
    queryFn: async () => {
      const { data } = await axios.get('/address')
      return data as Address[]
    },
    refetchInterval: 1000 * 60 * 2, // refetch every 2 mins
    refetchIntervalInBackground: false,
  })

  const addressCreation = useMutation({
    mutationKey: ['create-address'],
    mutationFn: async (newAddress: Address) => {
      await axios.post('/address', newAddress)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['addresses'] })

      addressCreation.reset()

      toast.success('Successfully Added Address')
    },
    onError: (error) => {
      toast.error(`An error occurred: ${error.message}`)
    },
  })

  const addressUpdation = useMutation({
    mutationKey: ['update-address'],
    mutationFn: async (addressToUpdate: Address) => {
      await axios.patch(`/address?id=eq.${addressToUpdate.id}`, addressToUpdate)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['addresses'] })

      addressUpdation.reset()

      toast.success('Successfully Updated Address')
    },
    onError: (error) => {
      toast.error(`An error occurred: ${error.message}`)
    },
  })

  const addressDeletion = useMutation({
    mutationKey: ['delete-address'],
    mutationFn: async (addressIdToDelete: number) => {
      await axios.delete(`/address?id=eq.${addressIdToDelete}`)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['addresses'] })

      addressDeletion.reset()

      toast.success('Successfully Deleted Address')
    },
    onError: (error) => {
      toast.error(`An error occurred: ${error.message}`)
    },
  })

  return {
    addresses,
    addressCreation,
    addressUpdation,
    addressDeletion,
  }
}
