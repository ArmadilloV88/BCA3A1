import {
  Calendar,
  ChartNoAxesColumnIncreasing,
  CircleGauge,
  Clock,
  Columns2,
  Columns3,
  ContactRound,
  DollarSign,
  Gauge,
  Mail,
  MapPin,
  Network,
  Phone,
  PiggyBank,
  QrCode,
  RectangleEllipsis,
  RectangleVertical,
  User,
} from 'lucide-react'
import Image from 'next/image'

import {
  Avatar,
  AvatarFallback,
  AvatarImage,
} from '@/components/shadcn-ui/avatar'
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
import { format } from 'date-fns'
import { Lesson } from '@/models/Lesson'
import { formatTime } from '@/lib/utils'
import useUsers from '@/hooks/useUsers'
import { useEffect } from 'react'
import { useQuery } from '@tanstack/react-query'
import { getUserById } from '@/server/actions'
import { User as ClerkUser } from '@clerk/nextjs/server'

// The following component was adapted from a v0 generation
// Link: https://v0.dev/

export default function LessonInformationCard({ lesson }: { lesson?: Lesson }) {
  // const { userById } = useUsers(lesson?.coachId)
  // userById.refetch()

  const userById = useQuery({
    queryKey: ['user'],
    queryFn: async () => {
      if (!lesson?.coachId) return null

      const res = await getUserById(lesson?.coachId)

      return JSON.parse(JSON.stringify(res)) as ClerkUser
    },
    refetchInterval: 1000 * 60 * 2, // refetch every 2 mins
    refetchIntervalInBackground: false,
  })

  useEffect(() => {
    async function refresh() {
      await userById.refetch()
    }

    // keep trying to refetch data until something is found
    if (userById.data) {
      return
    }

    refresh()
  }, [userById])

  return (
    <Card className="flex h-full w-full min-w-[448px] max-w-md flex-col">
      {lesson ? (
        <>
          <CardHeader>
            <div className="flex items-center space-x-4">
              <Avatar className="h-12 w-12">
                <AvatarImage
                  src={
                    userById?.data?.imageUrl
                      ? userById?.data.imageUrl
                      : '/placeholder-avatar.jpg'
                  }
                  alt="Coach's profile picture"
                />
                <AvatarFallback>CN</AvatarFallback>
              </Avatar>
              <div>
                <CardTitle>{lesson?.title} </CardTitle>
                {/* Maybe remove the sub heading */}
                <p className="text-sm text-muted-foreground">
                  Coach: {userById?.data?.firstName} {userById?.data?.lastName}
                </p>
              </div>
            </div>
          </CardHeader>
          <CardContent className="flex-grow space-y-4 overflow-auto">
            <div className="flex items-center space-x-2">
              <Calendar className="h-4 w-4 text-muted-foreground" />
              <span>{format(lesson?.date, 'eeee, dd MMMM yyyy')}</span>
            </div>

            {/* Fix start and end times */}
            <div className="flex items-center space-x-2">
              <Clock className="h-4 w-4 text-muted-foreground" />
              <span>
                {formatTime(lesson?.startTime)} - {formatTime(lesson?.endTime)}
              </span>
            </div>

            <div className="flex items-center space-x-2">
              <ChartNoAxesColumnIncreasing className="h-4 w-4 text-muted-foreground" />
              <Badge variant="secondary">{lesson?.skillLevel?.level} </Badge>
            </div>

            <Separator />
            <div>
              <h3 className="font-semibold">Contact Details</h3>
              <div className="mt-2 grid gap-2">
                {/* Fix contact number display */}
                <div className="flex items-center space-x-2">
                  <User className="h-4 w-4 text-muted-foreground" />
                  <span>{lesson?.contactDetails?.name}</span>
                </div>
                <div className="flex items-center space-x-2">
                  <Phone className="h-4 w-4 text-muted-foreground" />
                  <span>{lesson?.contactDetails?.phone}</span>
                </div>
                <div className="flex items-center space-x-2">
                  <Mail className="h-4 w-4 text-muted-foreground" />
                  <span>{lesson?.contactDetails?.email}</span>
                </div>
              </div>
            </div>
            <Separator />
            <div>
              <h3 className="font-semibold">Address</h3>
              <div className="mt-2 flex items-start space-x-2">
                <MapPin className="mt-1 h-4 w-4 text-muted-foreground" />
                <span>{lesson?.address?.address}</span>
              </div>
            </div>
            <Separator />
            <div>
              <h3 className="font-semibold">Description</h3>
              <p className="mt-2 text-sm text-muted-foreground">
                {lesson?.description}
              </p>
            </div>
            <Separator />
            <div>
              <h3 className="font-semibold">Payment Details</h3>

              <div className="mt-2 grid gap-2">
                <div className="flex items-center justify-between">
                  <span className="flex items-center">
                    <User className="mr-2 h-4 w-4 text-muted-foreground" />
                    Name:
                  </span>
                  <div className="flex items-center">
                    <span>{lesson?.paymentDetails?.name} </span>
                  </div>
                </div>

                <div className="flex items-center justify-between">
                  <span className="flex items-center">
                    <PiggyBank className="mr-2 h-4 w-4 text-muted-foreground" />
                    Bank:
                  </span>
                  <div className="flex items-center">
                    <span>{lesson?.paymentDetails?.bank} </span>
                  </div>
                </div>

                <div className="flex items-center justify-between">
                  <span className="flex items-center">
                    <Network className="mr-2 h-4 w-4 text-muted-foreground" />
                    Branch:
                  </span>
                  <div className="flex items-center">
                    <span>{lesson?.paymentDetails?.branch} </span>
                  </div>
                </div>

                <div className="flex items-center justify-between">
                  <span className="flex items-center">
                    <QrCode className="mr-2 h-4 w-4 text-muted-foreground" />
                    Branch Code:
                  </span>
                  <div className="flex items-center">
                    <span>{lesson?.paymentDetails?.branchCode} </span>
                  </div>
                </div>

                <div className="flex items-center justify-between">
                  <span className="flex items-center">
                    <RectangleEllipsis className="mr-2 h-4 w-4 text-muted-foreground" />
                    Account Number:
                  </span>
                  <div className="flex items-center">
                    <span>{lesson?.paymentDetails?.accountNumber} </span>
                  </div>
                </div>

                <div className="flex items-center justify-between">
                  <span className="flex items-center">
                    <DollarSign className="mr-2 h-4 w-4 text-muted-foreground" />
                    Lesson Fee:
                  </span>
                  <div className="flex items-center">
                    <span>R {lesson?.paymentAmount} </span>
                  </div>
                </div>
              </div>
            </div>

            {/* <div>
          Lorem ipsum dolor sit, amet consectetur adipisicing elit. Fuga a
          veniam blanditiis eius fugit dolore sequi accusantium corporis
          molestias quasi aliquid, consectetur mollitia quia quas ea, provident
          perspiciatis eaque sapiente at laudantium quod dignissimos. Id
          sapiente quisquam temporibus hic itaque libero, iure minima quia iusto
          distinctio expedita quidem. Molestiae voluptates eligendi dolor qui
          quos optio placeat ea dolore enim ab, impedit beatae quibusdam ex,
          veritatis animi nesciunt dolorem aliquam excepturi at officiis fugit
          provident, iste ullam? Laborum, quod. Reiciendis repellat repudiandae
          et vel architecto accusamus sit neque, modi itaque necessitatibus a
          dolores natus enim quia nihil distinctio incidunt laborum assumenda.
        </div>

        <div>
          Lorem ipsum dolor sit, amet consectetur adipisicing elit. Fuga a
          veniam blanditiis eius fugit dolore sequi accusantium corporis
          molestias quasi aliquid, consectetur mollitia quia quas ea, provident
          perspiciatis eaque sapiente at laudantium quod dignissimos. Id
          sapiente quisquam temporibus hic itaque libero, iure minima quia iusto
          distinctio expedita quidem. Molestiae voluptates eligendi dolor qui
          quos optio placeat ea dolore enim ab, impedit beatae quibusdam ex,
          veritatis animi nesciunt dolorem aliquam excepturi at officiis fugit
          provident, iste ullam? Laborum, quod. Reiciendis repellat repudiandae
          et vel architecto accusamus sit neque, modi itaque necessitatibus a
          dolores natus enim quia nihil distinctio incidunt laborum assumenda.
        </div> */}
          </CardContent>
        </>
      ) : (
        <div className="flex flex-grow items-center justify-center">
          Select a lesson to continue
        </div>
      )}

      {/* <CardFooter>
        <Button className="w-full">Confirm Booking</Button>
      </CardFooter> */}
    </Card>
  )
}
