'use client'

import React, { useEffect, useState } from 'react'
import { Separator } from '@/components/shadcn-ui/separator'
import { Button } from '@/components/shadcn-ui/button'
import LessonBookingCard from '@/components/ui/lesson-booking-card'
import { Lesson, LessonBooking } from '@/models/Lesson'
import LessonInformationCard from '@/components/ui/lesson-information-card'
import LessonSelectionCard from '@/components/ui/lesson-selection-card'
import useUsers from '@/hooks/useUsers'
import { useQuery } from '@tanstack/react-query'
import axios from '@/lib/axios'
import BookedLessonsCard from '@/components/ui/booked-lessons-card'
import { isAborted } from 'zod'
import { useUser } from '@clerk/nextjs'
import { format } from 'date-fns'

export default function page() {
  const [selectedLessonId, setSelectedLessonId] = useState<number | undefined>()
  const [selectedBookingId, setSelectedBookingId] = useState<
    number | undefined
  >()

  const { user, isSignedIn, isLoaded } = useUser()
  const isAdmin =
    user?.publicMetadata.userRole.id === 1 &&
    user?.publicMetadata.userRole.role === 'Admin'
  const isCoach =
    user?.publicMetadata.userRole.id === 2 &&
    user?.publicMetadata.userRole.role === 'Coach'

  // This is done to ensure everything is up to date
  const selectedLesson = useQuery({
    queryKey: ['selected-lesson'],
    queryFn: async () => {
      if (!selectedLessonId) return

      const { data } = await axios.get(
        `/lesson?id=eq.${selectedLessonId}&select=*,skillLevel(*),contactDetails(*),address(*),paymentDetails(*))`
      )

      return JSON.parse(JSON.stringify(data[0])) as Lesson
    },
    refetchInterval: 1000 * 30, // refetch 30 seconds to keep up to date
    refetchIntervalInBackground: false,
  })

  const selectedBooking = useQuery({
    queryKey: ['selected-booking'],
    queryFn: async () => {
      if (isLoaded) {
        if (!isSignedIn) {
          if (!selectedBookingId) return
        }
      }

      const url =
        (isAdmin || isCoach) && selectedBookingId !== undefined
          ? `/lessonBooking?id=eq.${selectedBookingId}&select=*,lesson(*,address(*),skillLevel(*),contactDetails(*),paymentDetails(*))`
          : `/lessonBooking?lesson.date=gte.(${format(new Date(), 'yyyy-MM-dd')})&userId=eq.(${user?.id!})&select=*,lesson(*,address(*),skillLevel(*),contactDetails(*),paymentDetails(*))`

      const { data } = await axios.get(url)

      return JSON.parse(JSON.stringify(data[0])) as LessonBooking
    },
    refetchInterval: 1000 * 30, // refetch 30 seconds to keep up to date
    refetchIntervalInBackground: false,
  })

  console.log(selectedBooking.data)

  useEffect(() => {
    async function refresh() {
      await selectedLesson.refetch()
    }

    // keep trying to refetch data until something is found
    if (selectedLesson.data) {
      return
    }

    refresh()
  }, [selectedLessonId])

  useEffect(() => {
    async function refresh() {
      await selectedBooking.refetch()
    }

    // keep trying to refetch data until something is found
    if (selectedBooking.data) {
      return
    }

    refresh()
  }, [selectedBookingId])

  return (
    <div
      /* className="flex h-full grow gap-5 bg-blue-500 "*/ className="flex flex-grow basis-0 justify-center gap-5"
    >
      {/* <div className="flex flex-grow basis-0 flex-col gap-2">
        <div className="flex-grow basis-0 overflow-auto bg-blue-500">
          Item 1 asdfasdfasdf asdf sadfa sdfasdf Lorem ipsum dolor sit amet
          consectetur adipisicing elit. Delectus dignissimos incidunt quibusdam
          blanditiis eos. Vitae dignissimos deserunt dicta illo voluptates ut
          neque, maxime exercitationem facere mollitia autem molestiae iure quam
          dolorum ipsam quasi suscipit optio deleniti, obcaecati eveniet sed.
          Tenetur laborum obcaecati distinctio ducimus ad dolor sit laboriosam
          ut totam et veniam quibusdam molestiae voluptate corrupti quos nihil
          nam repellendus temporibus pariatur, in dignissimos necessitatibus
          quam nisi? Debitis velit, veritatis voluptate dignissimos aliquid
          excepturi officia totam distinctio placeat! Architecto rem dicta
          repellendus distinctio accusamus consectetur sint voluptas saepe
          doloribus. Veritatis laboriosam, illum deleniti soluta molestiae
          voluptas doloremque non nam quas quidem corrupti fuga ad ipsa
          incidunt? Labore qui ad omnis aspernatur temporibus dolor recusandae
          molestias ducimus voluptatibus quam dolorum reprehenderit, autem est
          unde, beatae commodi fugit earum sed! At distinctio deleniti officiis
          quos, incidunt ab aliquam harum, voluptates ratione illum non error
          dignissimos minus magni expedita reprehenderit tenetur ducimus culpa
          et itaque veniam consectetur sunt vel iste. Quasi sunt consequuntur
          earum ut nam error rem eum voluptas labore dolor eligendi minus cum
          similique officia sint sapiente illo, ipsa dignissimos voluptate
          voluptates numquam. Rem beatae dolor quae explicabo unde eum tempore
          rerum sint animi ab numquam, necessitatibus autem. Veniam, obcaecati
          cumque? Item 1 asdfasdfasdf asdf sadfa sdfasdf Lorem ipsum dolor sit
          amet consectetur adipisicing elit. Delectus dignissimos incidunt
          quibusdam blanditiis eos. Vitae dignissimos deserunt dicta illo
          voluptates ut neque, maxime exercitationem facere mollitia autem
          molestiae iure quam dolorum ipsam quasi suscipit optio deleniti,
          obcaecati eveniet sed. Tenetur laborum obcaecati distinctio ducimus ad
          dolor sit laboriosam ut totam et veniam quibusdam molestiae voluptate
          corrupti quos nihil nam repellendus temporibus pariatur, in
          dignissimos necessitatibus quam nisi? Debitis velit, veritatis
          voluptate dignissimos aliquid excepturi officia totam distinctio
          placeat! Architecto rem dicta repellendus distinctio accusamus
          consectetur sint voluptas saepe doloribus. Veritatis laboriosam, illum
          deleniti soluta molestiae voluptas doloremque non nam quas quidem
          corrupti fuga ad ipsa incidunt? Labore qui ad omnis aspernatur
          temporibus dolor recusandae molestias ducimus voluptatibus quam
          dolorum reprehenderit, autem est unde, beatae commodi fugit earum sed!
          At distinctio deleniti officiis quos, incidunt ab aliquam harum,
          voluptates ratione illum non error dignissimos minus magni expedita
          reprehenderit tenetur ducimus culpa et itaque veniam consectetur sunt
          vel iste. Quasi sunt consequuntur earum ut nam error rem eum voluptas
          labore dolor eligendi minus cum similique officia sint sapiente illo,
          ipsa dignissimos voluptate voluptates numquam. Rem beatae dolor quae
          explicabo unde eum tempore rerum sint animi ab numquam, necessitatibus
          autem. Veniam, obcaecati cumque? Item 1 asdfasdfasdf asdf sadfa
          sdfasdf Lorem ipsum dolor sit amet consectetur adipisicing elit.
          Delectus dignissimos incidunt quibusdam blanditiis eos. Vitae
          dignissimos deserunt dicta illo voluptates ut neque, maxime
          exercitationem facere mollitia autem molestiae iure quam dolorum ipsam
          quasi suscipit optio deleniti, obcaecati eveniet sed. Tenetur laborum
          obcaecati distinctio ducimus ad dolor sit laboriosam ut totam et
          veniam quibusdam molestiae voluptate corrupti quos nihil nam
          repellendus temporibus pariatur, in dignissimos necessitatibus quam
          nisi? Debitis velit, veritatis voluptate dignissimos aliquid excepturi
          officia totam distinctio placeat! Architecto rem dicta repellendus
          distinctio accusamus consectetur sint voluptas saepe doloribus.
          Veritatis laboriosam, illum deleniti soluta molestiae voluptas
          doloremque non nam quas quidem corrupti fuga ad ipsa incidunt? Labore
          qui ad omnis aspernatur temporibus dolor recusandae molestias ducimus
          voluptatibus quam dolorum reprehenderit, autem est unde, beatae
          commodi fugit earum sed! At distinctio deleniti officiis quos,
          incidunt ab aliquam harum, voluptates ratione illum non error
          dignissimos minus magni expedita reprehenderit tenetur ducimus culpa
          et itaque veniam consectetur sunt vel iste. Quasi sunt consequuntur
          earum ut nam error rem eum voluptas labore dolor eligendi minus cum
          similique officia sint sapiente illo, ipsa dignissimos voluptate
          voluptates numquam. Rem beatae dolor quae explicabo unde eum tempore
          rerum sint animi ab numquam, necessitatibus autem. Veniam, obcaecati
          cumque?
        </div>
      </div> */}

      {/* Calendar and Lessons Column */}

      <div className="flex max-w-max flex-grow basis-0 flex-col items-center justify-center gap-2">
        <div className="flex-grow basis-0 overflow-auto">
          <LessonSelectionCard
            selectedLesson={selectedLessonId ? selectedLesson.data : undefined}
            setSelectedLessonId={setSelectedLessonId}
          />
        </div>
      </div>

      <Separator className="my-auto h-3/4 w-0.5" />

      <div className="flex max-w-max flex-grow basis-0 flex-col gap-2">
        <div className="flex-grow basis-0 overflow-auto">
          <LessonInformationCard
            lesson={selectedLessonId ? selectedLesson.data : undefined}
          />
        </div>
      </div>

      <div className="flex max-w-max flex-grow basis-0 flex-col gap-2">
        <div className="flex-grow basis-0 overflow-auto">
          <LessonBookingCard
            lessonBooking={selectedBookingId ? selectedBooking.data : undefined}
            lesson={
              selectedLessonId ? selectedLesson.data : undefined
              // selectedBookingId
              //   ? selectedBooking.data?.lesson
              //   : selectedLessonId
              //     ? selectedLesson.data
              //     : undefined
            }
          />
        </div>
      </div>

      <Separator className="my-auto h-3/4 w-0.5" />

      <div className="flex max-w-max flex-grow basis-0 flex-col items-center justify-center gap-2">
        <div className="flex-grow basis-0 overflow-auto">
          <BookedLessonsCard
            selectedBooking={
              selectedBookingId ? selectedBooking.data : undefined
            }
            selectedLesson={selectedLessonId ? selectedLesson.data : undefined}
            setSelectedBookingId={setSelectedBookingId}
          />
        </div>
      </div>

      {/* <div className="flex max-w-max flex-grow basis-0 flex-col gap-2">
        <div className="flex-grow basis-0 overflow-auto">
          Upcoming Lessons Section
          <div className="flex h-full w-72 max-w-xs flex-col gap-2 rounded-md border p-4">
            <p className="mx-auto">Your Upcoming Lessons</p>

            <Separator className="mx-auto h-0.5 w-3/4" />

            <div className="flex max-h-fit grow flex-col gap-20 overflow-auto bg-rose-500">
              Come back to
              <div className="bg-blue-500">Item 1</div>
              <div className="bg-blue-500">Item 1</div>
              <div className="bg-blue-500">Item 1</div>
              <div className="bg-blue-500">Item 1</div>
              <div className="bg-blue-500">Item 1</div>
            </div>
          </div>
        </div>
      </div> */}
    </div>
  )
}
