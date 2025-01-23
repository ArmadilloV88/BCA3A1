import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import axios from '@/lib/axios'
import { toast } from 'sonner'
import { Lesson, LessonBooking } from '@/models/Lesson'
import { format } from 'date-fns'

export default function useLessonBooking() {
  const queryClient = useQueryClient()

  const lessonBookings = useQuery({
    queryKey: ['lesson-bookings'],
    queryFn: async () => {
      const { data } = await axios.get(
        '/lessonBooking?select=*,lesson(*,address(*),skillLevel(*),contactDetails(*),paymentDetails(*))'
      )

      // ?select=*,skillLevel(*),contactDetails(*),address(*),paymentDetails(*)'
      return data as LessonBooking[]
    },
    refetchInterval: 1000 * 60 * 2, // refetch every 2 mins
    refetchIntervalInBackground: false,
  })

  const lessonBookingCreation = useMutation({
    mutationKey: ['create-lesson-booking'],
    mutationFn: async (newLesson: LessonBooking) => {
      // 1. get the latest id
      const res = await axios.get(`/lessonBooking?order=id.desc&limit=1`)
      const newId = res?.data[0]?.id ? res.data[0].id + 1 : 1

      // 2. update the lesson by adding the lessosn booking id for easier reference
      const { data } = await axios.get(`/lesson?id=eq.${newLesson.lessonId}`)

      const updatedBookingIds: { bookingIds: (number | undefined)[] } = {
        bookingIds: [],
      }

      if (!data[0].bookingIds) updatedBookingIds.bookingIds = [newId]
      else if (data[0].bookingIds?.length < data[0].totalSlots)
        updatedBookingIds.bookingIds = [...data[0].bookingIds, newId]

      await axios.patch(
        `/lesson?id=eq.${newLesson.lessonId}`,
        updatedBookingIds
      )

      // 3. save the new lesson
      await axios.post('/lessonBooking', newLesson)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['lesson-bookings'] })
      queryClient.invalidateQueries({ queryKey: ['lessons'] })
      queryClient.invalidateQueries({ queryKey: ['lessons-for-date'] })
      queryClient.invalidateQueries({ queryKey: ['selected-lesson'] })

      lessonBookingCreation.reset()

      toast.success('Successfully Added Lesson Booking')
    },
    onError: (error) => {
      console.log(error)
      toast.error(`An error occurred: ${error.message}`)
    },
  })

  const lessonBookingUpdation = useMutation({
    mutationKey: ['update-lesson-booking'],
    mutationFn: async (lessonToUpdate: LessonBooking) => {
      await axios.patch(
        `/lessonBooking?id=eq.${lessonToUpdate.id}`,
        lessonToUpdate
      )
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['lesson-bookings'] })
      queryClient.invalidateQueries({ queryKey: ['lessons-for-date'] })
      queryClient.invalidateQueries({ queryKey: ['selected-lesson'] })

      lessonBookingUpdation.reset()

      toast.success('Successfully Updated Booking')
    },
    onError: (error) => {
      toast.error(`An error occurred: ${error.message}`)
    },
  })

  // const lessonDeletion = useMutation({
  //   mutationKey: ['delete-lesson'],
  //   mutationFn: async (lessonToDelete: Lesson) => {
  //     await axios.delete(`/lesson?id=eq.${lessonToDelete.id}`)
  //   },
  //   onSuccess: () => {
  //     queryClient.invalidateQueries({ queryKey: ['lessons'] })

  //     lessonDeletion.reset()

  //     toast.success('Successfully Deleted Lesson')
  //   },
  //   onError: (error) => {
  //     toast.error(`An error occurred: ${error.message}`)
  //   },
  // })

  return {
    lessonBookings,
    lessonBookingCreation,
    lessonBookingUpdation,
    // lessonUpdation,
    // lessonDeletion,
  }
}
