import useLessonBooking from '@/hooks/useLessonBooking'
import { useUser } from '@clerk/nextjs'

import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/shadcn-ui/card'
import { Lesson, LessonBooking } from '@/models/Lesson'
import { Dispatch, SetStateAction } from 'react'
import { cn, formatDate, formatTime } from '@/lib/utils'
import { Badge } from '../shadcn-ui/badge'
import { ChartNoAxesColumnIncreasing } from 'lucide-react'
import { Separator } from '../shadcn-ui/separator'
import { Button } from '../shadcn-ui/button'

interface Props {
  selectedBooking: LessonBooking | undefined
  selectedLesson: Lesson | undefined
  setSelectedBookingId: Dispatch<SetStateAction<number | undefined>>
}

export default function BookedLessonsCard({
  selectedBooking,
  selectedLesson,
  setSelectedBookingId,
}: Props) {
  const { lessonBookings } = useLessonBooking()
  const { user } = useUser()

  const isAdmin =
    user?.publicMetadata.userRole.id === 1 &&
    user?.publicMetadata.userRole.role === 'Admin'
  const isCoach =
    user?.publicMetadata.userRole.id === 2 &&
    user?.publicMetadata.userRole.role === 'Coach'

  // selectedLesson && (isAdmin || isCoach)

  let canShowData = false

  if (!(isAdmin || isCoach)) {
    canShowData = true
  } else if ((isAdmin || isCoach) && selectedLesson !== undefined) {
    canShowData = true
  }

  return (
    <Card className="flex h-full w-full max-w-md flex-grow flex-col">
      <CardHeader className="flex flex-col gap-2">
        <p className="flex w-full items-center justify-center">
          {isAdmin || isCoach ? (
            <span>Lesson Bookings</span>
          ) : (
            <span>Your Upcoming Lessons</span>
          )}
        </p>

        <Separator className="mx-auto h-0.5 w-3/4" />
      </CardHeader>
      <CardContent className="flex-grow space-y-4 overflow-auto">
        {canShowData ? (
          lessonBookings.isLoading ? (
            <div className="flex w-full animate-pulse items-center justify-center">
              Loading...
            </div>
          ) : lessonBookings.isError ? (
            <div className="max-w-full text-wrap text-rose-500">
              Error: {lessonBookings.error.message}
            </div>
          ) : lessonBookings?.data?.length ? (
            lessonBookings?.data?.map((booking) => (
              <div
                key={booking.id}
                className={cn(
                  'flex w-full flex-col items-center justify-between gap-2 rounded-lg border p-3',
                  selectedBooking?.id === booking.id
                    ? 'border-2 border-primary'
                    : 'hover:bg-muted'
                )}
                onClick={() =>
                  setSelectedBookingId(
                    selectedBooking?.id === booking.id ? undefined : booking.id
                  )
                }
              >
                {/* <Badge>
                  <ChartNoAxesColumnIncreasing className="mr-2 h-4 w-4" />
                  {booking?.lesson?.skillLevel?.level}
                </Badge> */}

                {formatDate(
                  booking?.lesson?.date ? booking?.lesson?.date.toString() : ''
                )}

                <span className="text-sm">
                  {formatTime(booking?.lesson?.startTime)} -{' '}
                  {formatTime(booking?.lesson?.endTime)}
                </span>

                <Separator className="mx-auto w-3/5" />

                {booking.confirmationPending ? (
                  <Button size="sm" className="h-8 bg-black text-sm text-white">
                    Pending
                  </Button>
                ) : (
                  <Button
                    size="sm"
                    variant={
                      booking.paymentConfirmed ? 'default' : 'destructive'
                    }
                    className="h-8 text-sm"
                  >
                    {booking.paymentConfirmed ? 'Confirmed' : 'Denied'}
                  </Button>
                )}
              </div>
            ))
          ) : (
            <div className="flex w-full items-center justify-center">
              No bookings have been found
            </div>
          )
        ) : (
          <div className="flex w-full items-center justify-center">
            Select a lesson to continue
          </div>
        )}
      </CardContent>
    </Card>
  )
}
