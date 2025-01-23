import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import axios from '@/lib/axios'
import { toast } from 'sonner'
import { Lesson } from '@/models/Lesson'
import { format } from 'date-fns'
import { useUser } from '@clerk/nextjs'
import { getCurrentUser } from '@/server/actions'

export default function useLessons({
  selectedDate,
  selectedBookingId = undefined,
  useAdmin = undefined,
}: {
  selectedDate?: Date
  selectedBookingId?: number
  useAdmin?: boolean
}) {
  const queryClient = useQueryClient()

  const allLessons = useQuery({
    queryKey: ['lessons'],
    queryFn: async () => {
      const user = await getCurrentUser()

      const url =
        useAdmin && selectedBookingId
          ? `/lessonBooking?id=eq.${selectedBookingId}&select=*,lesson(*,address(*),skillLevel(*),contactDetails(*),paymentDetails(*))`
          : `/lessonBooking?lesson.date=gte.(${format(new Date(), 'yyyy-MM-dd')})&userId=eq.(${user?.id!})&select=*,lesson(*,address(*),skillLevel(*),contactDetails(*),paymentDetails(*))`

      const { data } = await axios.get(
        // '/lesson?select=*,skillLevel(*),contactDetails(*),address(*),paymentDetails(*)'
        url
      )

      return data as Lesson[]
    },
    refetchInterval: 1000 * 60 * 2, // refetch every 2 mins
    refetchIntervalInBackground: false,
  })

  const lessonsForDate = useQuery({
    queryKey: ['lessons-for-date'],
    queryFn: async () => {
      if (!selectedDate) return null

      const user = await getCurrentUser()
      const userData = user?.publicMetadata

      const url =
        userData?.userRole.id === 3 && userData?.userRole.role === 'Customer'
          ? `/lesson?date=eq.(${format(selectedDate, 'yyyy-MM-dd')})&skillLevelId=eq.${userData?.skillLevel.id}&select=*,skillLevel(*),contactDetails(*),address(*),paymentDetails(*))`
          : `/lesson?date=eq.(${format(selectedDate, 'yyyy-MM-dd')})&select=*,skillLevel(*),contactDetails(*),address(*),paymentDetails(*))`

      const { data } = await axios.get(url)

      return data as Lesson[]
    },
    refetchInterval: 1000 * 60 * 2, // refetch every 2 mins
    refetchIntervalInBackground: false,
  })

  const lessonCreation = useMutation({
    mutationKey: ['create-lesson'],
    mutationFn: async (newLesson: Lesson) => {
      await axios.post('/lesson', newLesson)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['lessons'] })
      queryClient.invalidateQueries({ queryKey: ['lessons-for-date'] })

      lessonCreation.reset()

      toast.success('Successfully Added Lesson')
    },
    onError: (error) => {
      toast.error(`An error occurred: ${error.message}`)
    },
  })

  const lessonUpdation = useMutation({
    mutationKey: ['update-lesson'],
    mutationFn: async (lessonToUpdate: Lesson) => {
      await axios.patch(`/lesson?id=eq.${lessonToUpdate.id}`, lessonToUpdate)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['lessons'] })

      lessonUpdation.reset()

      toast.success('Successfully Updated Address')
    },
    onError: (error) => {
      toast.error(`An error occurred: ${error.message}`)
    },
  })

  const lessonDeletion = useMutation({
    mutationKey: ['delete-lesson'],
    mutationFn: async (lessonToDelete: Lesson) => {
      await axios.delete(`/lesson?id=eq.${lessonToDelete.id}`)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['lessons'] })

      lessonDeletion.reset()

      toast.success('Successfully Deleted Lesson')
    },
    onError: (error) => {
      toast.error(`An error occurred: ${error.message}`)
    },
  })

  return {
    allLessons,
    lessonsForDate,
    lessonCreation,
    lessonUpdation,
    lessonDeletion,
  }
}
