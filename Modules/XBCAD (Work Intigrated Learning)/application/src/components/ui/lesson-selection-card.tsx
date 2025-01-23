import { Calendar } from '@/components/shadcn-ui/calendar'
import { cn, formatDate, formatTime } from '@/lib/utils'

import { Button } from '@/components/shadcn-ui/button'
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/shadcn-ui/card'
import { Separator } from '@/components/shadcn-ui/separator'
import { Badge } from '../shadcn-ui/badge'

import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/shadcn-ui/form'
import { Textarea } from '@/components/shadcn-ui/textarea'
import { Checkbox } from '@/components/shadcn-ui/checkbox'
// import { toast } from "@/components/ui/use-toast"

import { z } from 'zod'
import { Input } from '../shadcn-ui/input'
import { ScrollArea } from '../shadcn-ui/scroll-area'
import { Dispatch, SetStateAction, useEffect, useState } from 'react'
import { ChartNoAxesColumnIncreasing } from 'lucide-react'
import LessonFormDrawer from './lesson-form-drawer'
import useLessons from '@/hooks/useLessons'
import { Lesson } from '@/models/Lesson'
import { useUser } from '@clerk/nextjs'

interface Props {
  selectedLesson: Lesson | undefined
  setSelectedLessonId: Dispatch<SetStateAction<number | undefined>>
}

export default function LessonSelectionCard({
  selectedLesson,
  setSelectedLessonId,
}: Props) {
  const [selectedDate, setSelectedDate] = useState<Date | undefined>(new Date())
  const [formIsOpen, setFormIsOpen] = useState(false)
  const { user } = useUser()
  const {
    allLessons,
    lessonsForDate,
    lessonCreation,
    lessonUpdation,
    lessonDeletion,
  } = useLessons({ selectedDate })
  // const [selectedLesson, setSelectedLesson] = useState<Lesson | undefined>()

  const isAdmin =
    user?.publicMetadata.userRole.id === 1 &&
    user?.publicMetadata.userRole.role === 'Admin'
  const isCoach =
    user?.publicMetadata.userRole.id === 2 &&
    user?.publicMetadata.userRole.role === 'Coach'

  useEffect(() => {
    async function refetch() {
      await lessonsForDate.refetch()
    }

    refetch()
  }, [selectedDate])

  return (
    <>
      {/* max-w-max */}
      <section className="flex h-full w-full max-w-[278px] flex-grow flex-col gap-3 overflow-hidden">
        <Calendar
          mode="single"
          selected={selectedDate}
          onSelect={setSelectedDate}
          // REMOVE IF ADMIN / COACH
          fromDate={isAdmin || isCoach ? undefined : new Date()}
          className="max-w-max rounded-md border"
        />

        <Card className="flex w-full max-w-md flex-grow flex-col overflow-hidden">
          <CardHeader className="flex flex-col gap-2">
            <p className="flex w-full items-center justify-center">
              {selectedDate ? (
                <span>
                  {formatDate(selectedDate ? selectedDate?.toDateString() : '')}
                </span>
              ) : (
                <span>Please select a date</span>
              )}
            </p>

            <Separator className="mx-auto h-0.5 w-3/4" />
          </CardHeader>
          {selectedDate && (
            <>
              <CardContent className="flex-grow space-y-4 overflow-auto">
                {lessonsForDate.isLoading ? (
                  <div className="flex w-full animate-pulse items-center justify-center">
                    Loading...
                  </div>
                ) : lessonsForDate.isError ? (
                  <div className="max-w-full text-wrap text-rose-500">
                    Error: {lessonsForDate.error.message}
                  </div>
                ) : lessonsForDate?.data?.length ? (
                  lessonsForDate?.data?.map((lesson) => (
                    <div
                      key={lesson.id}
                      className={cn(
                        'flex w-full flex-col items-center justify-between gap-2 rounded-lg border p-3',
                        selectedLesson?.id === lesson.id
                          ? 'border-2 border-primary'
                          : 'hover:bg-muted'
                      )}
                      onClick={() =>
                        setSelectedLessonId((val) => {
                          return selectedLesson?.id === lesson.id
                            ? undefined
                            : lesson.id
                        })
                      }
                    >
                      <Badge>
                        <ChartNoAxesColumnIncreasing className="mr-2 h-4 w-4" />
                        {lesson.skillLevel?.level}
                      </Badge>

                      <Separator className="mx-auto w-3/5" />

                      <span className="text-sm">
                        {formatTime(lesson.startTime)} -{' '}
                        {formatTime(lesson.endTime)}
                      </span>
                    </div>
                  ))
                ) : (
                  <div className="flex w-full items-center justify-center">
                    No lessons have been found
                  </div>
                )}
              </CardContent>

              {/* Add button for admins / coaches only */}
              {(isAdmin || isCoach) && (
                <CardFooter className="mt-3">
                  <LessonFormDrawer
                    // mode={selectedLesson ? 'update' : 'create'}
                    mode={'create'}
                    lessonToUpdate={selectedLesson ? selectedLesson : undefined}
                    trigger={<Button className="w-full">Add Lesson</Button>}
                    isOpen={formIsOpen}
                    onOpenChanged={(newValue) => setFormIsOpen(() => newValue)}
                    itemAction={
                      /* selectedLesson ? lessonUpdation :  */ lessonCreation
                    }
                  />
                </CardFooter>
              )}
            </>
          )}
        </Card>
      </section>
    </>
  )
}
