import {
  BookOpenCheck,
  Calendar,
  ChartNoAxesColumnIncreasing,
  CircleGauge,
  Clock,
  CloudUpload,
  Columns2,
  Columns3,
  DollarSign,
  Gauge,
  Loader2,
  Mail,
  MapPin,
  Paperclip,
  Phone,
  RectangleVertical,
  ShieldCheck,
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
import { Lesson, LessonBooking } from '@/models/Lesson'
import { format } from 'date-fns'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'

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
import { supabase } from '@/lib/supabase-client'
import {
  FileInput,
  FileUploader,
  FileUploaderContent,
  FileUploaderItem,
} from '../shadcn-ui-extensions/file-upload'
import useLessonBooking from '@/hooks/useLessonBooking'
import { useUser } from '@clerk/nextjs'
import { nanoid } from 'nanoid'
import { toast } from 'sonner'
import Link from 'next/link'

// The following component was adapted from a v0 generation
// Link: https://v0.dev/

const MAX_FILE_SIZE = 4 * 1024 * 1024 // 4MB

const bookingSchema = z.object({
  // lessonId: z.number().int().positive(),
  // userId: z.number().int().positive(),
  // horseId: z.number().int().positive().optional(),
  paymentConfirmed: z.boolean(),

  usingPersonalHorse: z.boolean(),
  messageForCoach: z.string().max(500).optional(),

  // The following file validation was adapted from dev.to
  // Author: Prodipta Banerjee (https://dev.to/banerjeeprodipta)
  // Link: https://dev.to/banerjeeprodipta/validate-file-with-zod-20o
  proofOfPayment: z
    // .instanceof(File)
    .array(
      // This approach is used for the file drop and was adapted from Shadcn Extension.com
      // Author: Shadcn Extension
      // Link: https://shadcn-extension.vercel.app/docs/file-upload#accessibility
      z
        .instanceof(File)
        .refine((file) => file.size <= MAX_FILE_SIZE, `Max file size is 5MB.`)
        .refine(
          (file) =>
            ['image/jpeg', 'image/png', 'application/pdf'].includes(file.type),
          'Only .jpg, .png, and .pdf files are accepted.'
        )
    ),
  // .refine((file: File) => file?.length !== 0, "File is required")
})

const lessonBookingSchema = bookingSchema.partial({
  paymentConfirmed: true,
})

const bookingConfirmationSchema = bookingSchema.partial({
  proofOfPayment: true,
})
// .extend({
//   proofOfPayment: z
//     // .instanceof(File)
//     .array(
//       // This approach is used for the file drop and was adapted from Shadcn Extension.com
//       // Author: Shadcn Extension
//       // Link: https://shadcn-extension.vercel.app/docs/file-upload#accessibility
//       z
//         .instanceof(File)
//         .refine((file) => file.size <= MAX_FILE_SIZE, `Max file size is 5MB.`)
//         .refine(
//           (file) =>
//             ['image/jpeg', 'image/png', 'application/pdf'].includes(
//               file.type
//             ),
//           'Only .jpg, .png, and .pdf files are accepted.'
//         )
//     )
//     // .optional()
//     .nullable(),
// })
type LessonBookingFormData = z.infer<typeof lessonBookingSchema>
type BookingConfirmationFormData = z.infer<typeof bookingConfirmationSchema>

export default function LessonBookingCard({
  lesson,
  lessonBooking,
}: {
  lesson?: Lesson
  lessonBooking?: LessonBooking
}) {
  const { lessonBookingCreation, lessonBookingUpdation } = useLessonBooking()
  const { user } = useUser()
  const isAdmin =
    user?.publicMetadata.userRole.id === 1 &&
    user?.publicMetadata.userRole.role === 'Admin'
  const isCoach =
    user?.publicMetadata.userRole.id === 2 &&
    user?.publicMetadata.userRole.role === 'Coach'

  const form = useForm<LessonBookingFormData | BookingConfirmationFormData>({
    resolver: zodResolver(
      isAdmin || isCoach ? bookingConfirmationSchema : lessonBookingSchema
    ),
    defaultValues:
      (isAdmin || isCoach) && lessonBooking !== undefined
        ? {
            // lessonId: 0,
            // userId: 0,
            paymentConfirmed: false,
            usingPersonalHorse: lessonBooking?.usingPersonalHorse,
            messageForCoach: lessonBooking?.messageForCoach,
          }
        : {
            // lessonId: 0,
            // userId: 0,
            paymentConfirmed: false,
            usingPersonalHorse: true,
          },
  })

  const dropZoneConfig = {
    maxFiles: 1,
    maxSize: 1024 * 1024 * 4, // 4MB
    multiple: false,
  }

  async function onSubmit(
    data: BookingConfirmationFormData | LessonBookingFormData
  ) {
    // setIsSubmitting(true)
    try {
      if ((isAdmin || isCoach) && lessonBooking !== undefined) {
        lessonBooking.confirmationPending = false
        lessonBooking.paymentConfirmed = data.paymentConfirmed!

        lessonBookingUpdation.mutate(lessonBooking)
        return
      }

      const fileUploadPath = `${user?.id}/${nanoid()}`

      await supabase.storage
        .from('proof-of-payments')
        .upload(fileUploadPath, data?.proofOfPayment![0])

      const publicImageUrl = `https://kdrvzeievzbcxhjnkzwr.supabase.co/storage/v1/object/public/proof-of-payments/${fileUploadPath}`

      const newLessonBooking: LessonBooking = {
        lessonId: lesson?.id!,
        userId: user?.id!,
        usingPersonalHorse: data.usingPersonalHorse,
        proofOfPayment: publicImageUrl,
        confirmationPending: true,
        paymentConfirmed: false,
        messageForCoach: data?.messageForCoach,
      }

      lessonBookingCreation.mutate(newLessonBooking)

      form.reset()

      // if (itemAction.isIdle && !itemAction.isError && onOpenChanged) {
      //   onOpenChanged(false)
      // }
    } catch (error) {
      console.log(error)
      toast.error(`An error occurred: ${error}`)
    }
  }

  // console.log(
  //   'TEST SOMETHING: ',
  //   (isAdmin || isCoach) && lessonBooking !== undefined
  // )

  let canShowForm = false

  if (lesson !== undefined && !(isAdmin || isCoach)) {
    canShowForm = true
  } else if (
    (isAdmin || isCoach) &&
    lesson !== undefined &&
    lessonBooking !== undefined
  ) {
    canShowForm = true
  }

  //   let disableButton = false

  // if(){
  //  disableButton = true
  // } else if(){

  // }

  //    ||
  //   ((isAdmin || isCoach) && lessonBooking ? true : false)

  return (
    //(!((isAdmin || isCoach) && lessonBooking !== undefined) && lesson) ||
    <Card className="flex h-full w-full min-w-[448px] max-w-md flex-grow flex-col">
      {canShowForm ? (
        <>
          <CardHeader className="flex flex-col gap-2">
            <CardTitle className="flex w-full items-center justify-center">
              Book A Slot
            </CardTitle>

            <Separator className="mx-auto h-0.5 w-3/4" />
          </CardHeader>

          <CardContent
            /* className="grid gap-4 overflow-hidden"  */ className="flex flex-grow overflow-auto"
          >
            <Form {...form}>
              <form
                id="booking-form"
                onSubmit={form.handleSubmit(onSubmit)}
                className="space-y-6"
              >
                {/* <ScrollArea className="w-full space-y-8"> */}

                {/* <div className="flex-grow space-y-8 overflow-auto"> */}
                {/* <FormField
                  control={form.control}
                  name="horseId"
                  render={({ field }) => (
                    // Update following form field to show and select horses
                    <FormItem>
                      <FormLabel>Horse To Use</FormLabel>
                      <FormControl>
                        <Input
                          type="number"
                          {...field}
                          onChange={(e) =>
                            field.onChange(
                              e.target.value
                                ? parseInt(e.target.value)
                                : undefined
                            )
                          }
                        />
                      </FormControl>
                      <FormDescription>
                        Select the horse you'll be using (if applicable).
                      </FormDescription>
                      <FormMessage />
                    </FormItem>
                  )}
                /> */}

                <FormField
                  control={form.control}
                  name="usingPersonalHorse"
                  render={({ field }) => (
                    <FormItem className="flex flex-row items-start space-x-3 space-y-0 rounded-md">
                      <FormLabel className="flex flex-row items-start space-x-3 space-y-0 rounded-md border p-4">
                        <FormControl>
                          <Checkbox
                            className="disabled:opacity-100"
                            disabled={
                              (isAdmin || isCoach) && lessonBooking
                                ? true
                                : false
                            }
                            // manually adding the value as the default values approach doesnt seem to work
                            checked={
                              (isAdmin || isCoach) && lessonBooking
                                ? lessonBooking.usingPersonalHorse
                                : field.value
                            }
                            onCheckedChange={field.onChange}
                          />
                        </FormControl>
                        <div className="space-y-1 leading-none">
                          <FormLabel>Using Personal Horse</FormLabel>
                          <FormDescription>
                            Check this box if you'll be using your personal
                            horse.
                          </FormDescription>
                        </div>
                      </FormLabel>
                    </FormItem>
                  )}
                />

                <Separator className="mx-auto w-3/4" />

                <FormField
                  control={form.control}
                  name="messageForCoach"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Message</FormLabel>
                      <FormControl>
                        <Textarea
                          className="disabled:opacity-100"
                          {...field}
                          disabled={
                            (isAdmin || isCoach) && lessonBooking ? true : false
                          }
                          // manually adding the value as the default values approach doesnt seem to work
                          value={
                            (isAdmin || isCoach) && lessonBooking
                              ? lessonBooking?.messageForCoach
                              : field.value
                          }
                        />
                      </FormControl>
                      <FormDescription>
                        Leave an optional message (max 500 characters).
                      </FormDescription>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <Separator className="mx-auto w-3/4" />

                <FormField
                  control={form.control}
                  name="proofOfPayment"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>
                        Proof Of Payment <span className="text-red-500">*</span>
                      </FormLabel>
                      <FormControl>
                        {(isAdmin || isCoach) && lessonBooking ? (
                          <Button variant={'link'} asChild>
                            <Link
                              target="_blank"
                              href={lessonBooking.proofOfPayment}
                            >
                              View Proof Of Payment
                            </Link>
                          </Button>
                        ) : (
                          <FileUploader
                            unselectable={
                              (isAdmin || isCoach) && lessonBooking
                                ? 'on'
                                : 'off'
                            }
                            // @ts-expect-error
                            value={field.value}
                            // {...field}
                            onValueChange={field.onChange}
                            dropzoneOptions={dropZoneConfig}
                            className="relative rounded-lg bg-background p-2"
                          >
                            <FileInput
                              id="fileInput"
                              className="outline-dashed outline-1 outline-slate-500"
                            >
                              <div className="flex w-full flex-col items-center justify-center p-8">
                                <CloudUpload className="h-10 w-10 text-gray-500" />
                                <p className="mb-1 text-sm text-gray-500 dark:text-gray-400">
                                  <span className="font-semibold">
                                    Click to upload
                                  </span>
                                  &nbsp; or drag and drop
                                </p>
                                <p className="text-xs text-gray-500 dark:text-gray-400">
                                  SVG, PNG, JPG or GIF
                                </p>
                              </div>
                            </FileInput>
                            <FileUploaderContent>
                              {field.value &&
                                field.value.length > 0 &&
                                field.value.map((file, i) => (
                                  <FileUploaderItem key={i} index={i}>
                                    <Paperclip className="h-4 w-4 stroke-current" />
                                    <span>{file.name}</span>
                                  </FileUploaderItem>
                                ))}
                            </FileUploaderContent>
                          </FileUploader>
                        )}
                      </FormControl>
                      {!(isAdmin || isCoach) && (
                        <FormDescription>
                          Upload your file here. Accepted formats: .jpg, .png,
                          .pdf. Max size: 4MB. Max files: 1.
                        </FormDescription>
                      )}
                      <FormMessage />
                    </FormItem>
                  )}
                />

                {/* Show the following for admins / coaches */}
                {(isAdmin || isCoach) && lessonBooking && (
                  <FormField
                    control={form.control}
                    name="paymentConfirmed"
                    render={({ field }) => (
                      <FormItem className="flex flex-row items-start space-x-3 space-y-0 rounded-md">
                        <FormLabel className="flex flex-row items-start space-x-3 space-y-0 rounded-md border p-4">
                          <FormControl>
                            <Checkbox
                              checked={field.value}
                              onCheckedChange={field.onChange}
                            />
                          </FormControl>
                          <div className="space-y-1 leading-none">
                            <FormLabel>Payment Confirmed</FormLabel>
                            <FormDescription>
                              Check this box if you have confirmed your payment.
                            </FormDescription>
                          </div>
                        </FormLabel>
                      </FormItem>
                    )}
                  />
                )}

                {/* <FormField
            control={form.control}
            name="proofOfPayment"
            render={({ field: { onChange, value, ...rest } }) => (
              <FormItem>
                <FormLabel>
                  Proof Of Payment <span className="text-red-500">*</span>
                </FormLabel>
                <FormControl>
                  <Input
                    type="file"
                    accept=".jpg,.png,.pdf"
                    onChange={(e) => {
                      const file = e.target.files?.[0]
                      if (file) {
                        onChange(file)
                      }
                    }}
                    {...rest}
                  />
                </FormControl>
                <FormDescription>
                  Upload your file here. Accepted formats: .jpg, .png, .pdf.
                  Max size: 4MB.
                </FormDescription>
                <FormMessage />
              </FormItem>
            )}
          /> */}

                {/* <div>
              Lorem ipsum dolor sit, amet consectetur adipisicing elit. Fuga a
              veniam blanditiis eius fugit dolore sequi accusantium corporis
              molestias quasi aliquid, consectetur mollitia quia quas ea,
              provident perspiciatis eaque sapiente at laudantium quod
              dignissimos. Id sapiente quisquam temporibus hic itaque libero,
              iure minima quia iusto distinctio expedita quidem. Molestiae
              voluptates eligendi dolor qui quos optio placeat ea dolore enim
              ab, impedit beatae quibusdam ex, veritatis animi nesciunt
              dolorem aliquam excepturi at officiis fugit provident, iste
              ullam? Laborum, quod. Reiciendis repellat repudiandae et vel
              architecto accusamus sit neque, modi itaque necessitatibus a
              dolores natus enim quia nihil distinctio incidunt laborum
              assumenda.
            </div>

            <div>
              Lorem ipsum dolor sit, amet consectetur adipisicing elit. Fuga a
              veniam blanditiis eius fugit dolore sequi accusantium corporis
              molestias quasi aliquid, consectetur mollitia quia quas ea,
              provident perspiciatis eaque sapiente at laudantium quod
              dignissimos. Id sapiente quisquam temporibus hic itaque libero,
              iure minima quia iusto distinctio expedita quidem. Molestiae
              voluptates eligendi dolor qui quos optio placeat ea dolore enim
              ab, impedit beatae quibusdam ex, veritatis animi nesciunt
              dolorem aliquam excepturi at officiis fugit provident, iste
              ullam? Laborum, quod. Reiciendis repellat repudiandae et vel
              architecto accusamus sit neque, modi itaque necessitatibus a
              dolores natus enim quia nihil distinctio incidunt laborum
              assumenda.
            </div> */}
                {/* </ScrollArea> */}
                {/* </div> */}

                {/* <Button className="w-full">Confirm Booking</Button> */}
              </form>
            </Form>
          </CardContent>
          <CardFooter className="mt-3 flex flex-col gap-2">
            {/* 
            The following button was adapted from stackoverflow.com  
            Author: Slidein (https://stackoverflow.com/users/15000290/slidein)
            Link: https://stackoverflow.com/questions/70103266/submiting-form-using-a-button-outside-the-form
            
            */}
            <div className="flex w-full flex-col items-center justify-center">
              <span className="text-sm">Slots:</span>
              <span>
                {lesson?.bookingIds.length} Taken | {lesson?.totalSlots} Total
              </span>
            </div>

            <Button
              className="w-full"
              form="booking-form"
              disabled={
                lesson?.bookingIds?.length! >= lesson?.totalSlots! &&
                !(isAdmin || isCoach)
              }
            >
              {/* 'Verfify Booking' */}

              {(isAdmin || isCoach) && lessonBooking ? (
                lessonBookingUpdation.isPending ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    <span>Verfifying Booking...</span>
                  </>
                ) : (
                  <>
                    <ShieldCheck className="mr-1 size-4" />
                    <span>Verfify Booking</span>
                  </>
                )
              ) : lessonBookingCreation.isPending ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  <span>Creating Booking...</span>
                </>
              ) : (
                <>
                  <BookOpenCheck className="mr-1 size-4" />
                  <span>
                    {lesson?.bookingIds?.length! >= lesson?.totalSlots!
                      ? 'All slots have been booked'
                      : 'Confirm Booking'}
                  </span>
                </>
              )}
            </Button>
          </CardFooter>
        </>
      ) : (
        <div className="flex flex-grow items-center justify-center">
          {/* {(isAdmin || isCoach) && lessonBooking !== undefined
            ? 'Select a lesson booking to continue'
            : 'Select a lesson to continue'} */}

          {lesson === undefined
            ? 'Select a lesson to continue'
            : (isAdmin || isCoach) &&
              lessonBooking === undefined &&
              'Select a lesson booking to continue'}
        </div>
      )}
    </Card>
  )
}
