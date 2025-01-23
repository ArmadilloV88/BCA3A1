import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import axios from '@/lib/axios'
import { PaymentDetails } from '@/models/payment-details'
import { toast } from 'sonner'

export default function usePaymentDetails() {
  const queryClient = useQueryClient()

  const paymentDetails = useQuery({
    queryKey: ['payment-details'],
    queryFn: async () => {
      const { data } = await axios.get('/paymentDetails')
      return data as PaymentDetails[]
    },
    refetchInterval: 1000 * 60 * 2, // refetch every 2 mins
    refetchIntervalInBackground: false,
  })

  const paymentDetailsCreation = useMutation({
    mutationKey: ['create-payment-details'],
    mutationFn: async (newPaymentDetails: PaymentDetails) => {
      await axios.post('/paymentDetails', newPaymentDetails)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['payment-details'] })

      paymentDetailsCreation.reset()

      toast.success('Successfully Added Payment Details')
    },
    onError: (error) => {
      toast.error(`An error occurred: ${error.message}`)
    },
  })

  const paymentDetailsUpdation = useMutation({
    mutationKey: ['update-payment-details'],
    mutationFn: async (paymentDetailsToUpdate: PaymentDetails) => {
      await axios.patch(
        `/paymentDetails?id=eq.${paymentDetailsToUpdate.id}`,
        paymentDetailsToUpdate
      )
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['payment-details'] })

      paymentDetailsUpdation.reset()

      toast.success('Successfully Updated Payment Details')
    },
    onError: (error) => {
      toast.error(`An error occurred: ${error.message}`)
    },
  })

  const paymentDetailsDeletion = useMutation({
    mutationKey: ['delete-payment-details'],
    mutationFn: async (paymentDetailsIdToDelete: number) => {
      await axios.delete(`/paymentDetails?id=eq.${paymentDetailsIdToDelete}`)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['payment-details'] })

      paymentDetailsDeletion.reset()

      toast.success('Successfully Deleted Payment Details')
    },
    onError: (error) => {
      toast.error(`An error occurred: ${error.message}`)
    },
  })

  return {
    paymentDetails,
    paymentDetailsCreation,
    paymentDetailsUpdation,
    paymentDetailsDeletion,
  }
}
