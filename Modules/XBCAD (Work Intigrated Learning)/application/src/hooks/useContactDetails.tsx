import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import axios from '@/lib/axios'
import { ContactDetails } from '@/models/contact-details'
import { toast } from 'sonner'

export default function useContactDetails() {
  const queryClient = useQueryClient()

  const contactDetails = useQuery({
    queryKey: ['contact-details'],
    queryFn: async () => {
      const { data } = await axios.get('/contactDetails')
      return data as ContactDetails[]
    },
    refetchInterval: 1000 * 60 * 2, // refetch every 2 mins
    refetchIntervalInBackground: false,
  })

  const contactDetailsCreation = useMutation({
    mutationKey: ['create-contact-details'],
    mutationFn: async (newContactDetails: ContactDetails) => {
      await axios.post('/contactDetails', newContactDetails)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['contact-details'] })

      contactDetailsCreation.reset()

      toast.success('Successfully Added Contact Details')
    },
    onError: (error) => {
      toast.error(`An error occurred: ${error.message}`)
    },
  })

  const contactDetailsUpdation = useMutation({
    mutationKey: ['update-contact-details'],
    mutationFn: async (contactDetailsToUpdate: ContactDetails) => {
      await axios.patch(
        `/contactDetails?id=eq.${contactDetailsToUpdate.id}`,
        contactDetailsToUpdate
      )
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['contact-details'] })

      contactDetailsUpdation.reset()

      toast.success('Successfully Updated Contact Details')
    },
    onError: (error) => {
      toast.error(`An error occurred: ${error.message}`)
    },
  })

  const contactDetailsDeletion = useMutation({
    mutationKey: ['delete-contact-details'],
    mutationFn: async (contactDetailsIdToDelete: number) => {
      await axios.delete(`/contactDetails?id=eq.${contactDetailsIdToDelete}`)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['contact-details'] })

      contactDetailsDeletion.reset()

      toast.success('Successfully Deleted Contact Details')
    },
    onError: (error) => {
      toast.error(`An error occurred: ${error.message}`)
    },
  })

  return {
    contactDetails,
    contactDetailsCreation,
    contactDetailsUpdation,
    contactDetailsDeletion,
  }
}
