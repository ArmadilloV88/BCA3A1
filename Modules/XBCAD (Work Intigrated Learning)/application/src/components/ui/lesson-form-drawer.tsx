'use client'

import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import * as z from 'zod'
import { Button } from '@/components/shadcn-ui/button'
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/shadcn-ui/form'
import { Input } from '@/components/shadcn-ui/input'
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from '@/components/shadcn-ui/select'

import {
  Drawer,
  DrawerTrigger,
  DrawerClose,
  DrawerContent,
  DrawerHeader,
  DrawerFooter,
  DrawerTitle,
  DrawerDescription,
} from '@/components/shadcn-ui/drawer'
import {
  CalendarIcon,
  CirclePlus,
  Loader2,
  Network,
  Pencil,
  PiggyBank,
  QrCode,
  RectangleEllipsis,
  User,
} from 'lucide-react'
import { ReactNode } from 'react'
import { UseMutationResult } from '@tanstack/react-query'
import { Textarea } from '../shadcn-ui/textarea'
import { Lesson } from '@/models/Lesson'
import { compareAsc, format, isAfter } from 'date-fns'
import { Popover, PopoverContent, PopoverTrigger } from '../shadcn-ui/popover'
import { Calendar } from '../shadcn-ui/calendar'
import { TimePicker } from './time-picker'
import useAddresses from '@/hooks/useAddresses'
import useContactDetails from '@/hooks/useContactDetails'
import usePaymentDetails from '@/hooks/usePaymentDetails'

import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/shadcn-ui/tooltip'
import {
  HoverCard,
  HoverCardContent,
  HoverCardTrigger,
} from '@/components/shadcn-ui/hover-card'
import { Separator } from '../shadcn-ui/separator'
import useUsers from '@/hooks/useUsers'
import useSkillLevels from '@/hooks/useSkillLevels'

const lessonSchema = z
  .object({
    title: z
      .string()
      .min(2, {
        message: 'Title must be at least 2 characters.',
      })
      .max(100, 'Title must be 100 characters or less'),
    description: z
      .string()
      .min(10, {
        message: 'Description must be at least 10 characters.',
      })
      .max(500, 'Description must be 500 characters or less'),
    date: z.date({
      required_error: 'A date is required.',
    }),
    startTime: z.coerce.date({
      required_error: 'Start time is required.',
    }),
    endTime: z.coerce.date({
      required_error: 'End time is required.',
    }),
    paymentAmount: z.coerce
      .number({
        required_error: 'Payment Amount is required',
        invalid_type_error: 'Payment Amount is required',
      })
      .step(0.01, { message: 'Amount must not be longer than 2 decimals' })
      .positive()
      .finite()
      .max(1000000, { message: 'Amount must not be more than 1 000 000' }),
    skillLevelId: z.coerce
      .number()
      .int()
      .positive('Skill Level must be a positive integer'),

    addressId: z
      .number()
      .int()
      .positive('Address ID must be a positive integer'),
    contactDetailsId: z
      .number()
      .int()
      .positive('Contact Details ID must be a positive integer'),
    paymentDetailsId: z
      .number()
      .int()
      .positive('Payment Details ID must be a positive integer'),
    coachId: z.string().min(1, 'Coach ID must be a selected'),
    totalSlots: z
      .number()
      .int()
      .positive('Total slots must be a positive integer')
      .max(20, "Can't have more than 20 slots"),
  })
  // The following refine was adapted from youtube.com
  // Author: Leigh Halliday (https://www.youtube.com/@leighhalliday)
  // Link: https://www.youtube.com/watch?v=_K34O0NcKAM
  .refine((data) => isAfter(data.endTime, data.startTime), {
    message: "End can't be before Start",
    path: ['endTime'],
  })

interface Props {
  mode?: 'create' | 'update'
  lessonToUpdate?: Lesson
  trigger?: ReactNode
  isOpen?: boolean
  onOpenChanged?: (open: boolean) => void
  itemAction: UseMutationResult<void, Error, Lesson, unknown>
}

export default function LessonFormDrawer({
  mode = 'create',
  lessonToUpdate,
  trigger,
  isOpen,
  onOpenChanged,
  itemAction,
}: Props) {
  // const form = useForm<Lesson>({
  //   resolver: zodResolver(paymentDetailsSchema),
  //   defaultValues:
  //     mode === 'update' && paymentDetailsToUpdate
  //       ? { paymentDetails: paymentDetailsToUpdate?.paymentDetails }
  //       : { paymentDetails: '' },
  // })

  const { users } = useUsers()
  const { skillLevels } = useSkillLevels()
  const { addresses } = useAddresses()
  const { contactDetails } = useContactDetails()
  const { paymentDetails } = usePaymentDetails()

  const form = useForm<Lesson>({
    resolver: zodResolver(lessonSchema),
    defaultValues:
      mode === 'update' && lessonToUpdate
        ? {
            title: lessonToUpdate.title,
            description: lessonToUpdate.description,
            date: lessonToUpdate.date,
            startTime: lessonToUpdate.startTime,
            endTime: lessonToUpdate.endTime,
            paymentAmount: lessonToUpdate.paymentAmount,
            skillLevelId: lessonToUpdate.skillLevelId,
            addressId: lessonToUpdate.addressId,
            contactDetailsId: lessonToUpdate.contactDetailsId,
            paymentDetailsId: lessonToUpdate.paymentDetailsId,
            coachId: lessonToUpdate.coachId,
            totalSlots: lessonToUpdate.totalSlots,
          }
        : {
            title: '',
            description: '',
            date: new Date(),
            // startTime: new Date(),
            // endTime: new Date(),
            paymentAmount: 0.0,
            skillLevelId: 0,
            addressId: 0,
            contactDetailsId: 0,
            paymentDetailsId: 0,
            coachId: '',
            totalSlots: 4,
          },
  })

  const onSubmit = (data: Lesson) => {
    data.startTime.setDate(data.date.getDate())
    data.startTime.setMonth(data.date.getMonth())
    data.startTime.setFullYear(data.date.getFullYear())

    data.endTime.setDate(data.date.getDate())
    data.endTime.setMonth(data.date.getMonth())
    data.endTime.setFullYear(data.date.getFullYear())

    console.log(data)

    if (mode === 'create') itemAction.mutate(data)
    else if (lessonToUpdate) {
      data.id = lessonToUpdate?.id
      itemAction.mutate(data)
    }

    form.reset()

    if (itemAction.isIdle && !itemAction.isError && onOpenChanged) {
      onOpenChanged(false)
    }
  }

  return (
    <Drawer direction="left" open={isOpen} onOpenChange={onOpenChanged}>
      <DrawerTrigger asChild>
        {trigger ? (
          trigger
        ) : (
          <Button size="sm" className="hidden h-8 lg:flex">
            <CirclePlus className="mr-1 size-4" />
            Add a Lesson
          </Button>
        )}
      </DrawerTrigger>
      <DrawerContent
        className="fixed inset-x-auto bottom-2 left-2 top-2 mt-0 flex w-[350px] rounded-md outline-none after:!content-none"
        style={
          {
            '--initial-transform': 'calc(100% + 8px)',
          } as React.CSSProperties
        }
      >
        <DrawerHeader>
          <DrawerTitle>
            {mode === 'create' ? 'Add a' : 'Update'} Lesson
          </DrawerTitle>
          <DrawerDescription>
            Fill in the following fields to{' '}
            {mode === 'create'
              ? 'add a new lesson'
              : 'update a lessons details'}
          </DrawerDescription>
        </DrawerHeader>

        <div className="h-full overflow-auto p-5">
          <Form {...form}>
            <form
              onSubmit={form.handleSubmit(onSubmit)}
              className="space-y-5"
              id="lesson-creation-form"
            >
              <FormField
                control={form.control}
                name="coachId"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Coach</FormLabel>
                    <Select
                      // onValueChange={(value) => field.onChange(parseInt(value))}
                      // defaultValue={field.value.toString()}

                      onValueChange={field.onChange}
                      defaultValue={field.value.toString()}
                    >
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="Select a coach" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        {/* fix coach selection */}
                        {/* {mockCoaches.map((coach) => (
                          <SelectItem
                            key={coach.id}
                            value={coach.id.toString()}
                          >
                            {coach.name}
                          </SelectItem>
                        ))} */}

                        {users.data
                          ?.filter(
                            (val) =>
                              val.publicMetadata.userRole.role === 'Coach' ||
                              val.publicMetadata.userRole.id === 2
                          )
                          .map((coach) => {
                            return (
                              <SelectItem key={coach.id} value={coach.id}>
                                {coach.firstName} {coach.lastName}
                              </SelectItem>
                            )
                          })}
                      </SelectContent>
                    </Select>
                    <FormDescription>The coach for the lesson.</FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="skillLevelId"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Level</FormLabel>
                    <Select
                      onValueChange={field.onChange}
                      defaultValue={field.value.toString()}
                    >
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="Select a level" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        {skillLevels?.data?.map((level) => (
                          <SelectItem
                            key={level.id}
                            value={level.id.toString()}
                          >
                            {level.level}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="totalSlots"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Total Slots</FormLabel>
                    <FormControl>
                      <Input
                        type="number"
                        {...field}
                        onChange={(e) =>
                          field.onChange(parseInt(e.target.value))
                        }
                      />
                    </FormControl>
                    <FormDescription>
                      The total number of slots available for this lesson.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <Separator className="mx-auto w-3/5" />

              <FormField
                control={form.control}
                name="title"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Title</FormLabel>
                    <FormControl>
                      <Input placeholder="Lesson title" {...field} />
                    </FormControl>
                    <FormDescription>
                      Enter the title of the lesson.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="description"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Description</FormLabel>
                    <FormControl>
                      <Textarea
                        placeholder="Describe the lesson"
                        className="resize-none"
                        {...field}
                      />
                    </FormControl>
                    <FormDescription>
                      Provide a brief description of the lesson.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <Separator className="mx-auto w-3/5" />

              <FormField
                control={form.control}
                name="date"
                render={({ field }) => (
                  <FormItem className="flex flex-col">
                    <FormLabel>Date</FormLabel>
                    <Popover>
                      <PopoverTrigger asChild>
                        <FormControl>
                          <Button
                            variant={'outline'}
                            className={`w-full pl-3 text-left font-normal ${
                              !field.value && 'text-muted-foreground'
                            }`}
                          >
                            {field.value ? (
                              format(field.value, 'PPP')
                            ) : (
                              <span>Pick a date</span>
                            )}
                            <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                          </Button>
                        </FormControl>
                      </PopoverTrigger>
                      <PopoverContent className="w-auto p-0" align="start">
                        <Calendar
                          mode="single"
                          selected={field.value}
                          onSelect={field.onChange}
                          fromDate={new Date()}
                          initialFocus
                        />
                      </PopoverContent>
                    </Popover>
                    <FormDescription>
                      Select the date for the lesson.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <div className="flex space-x-4">
                <FormField
                  control={form.control}
                  name="startTime"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Start Time</FormLabel>
                      <FormControl>
                        <TimePicker
                          date={field.value}
                          setDate={field.onChange}
                          {...field}
                        />

                        {/* <Input
                          type="time"
                          {...field}
                          // The "value" fix was taken from github.com
                          // Author: rafael-jordao (https://github.com/rafael-jordao)
                          // Link: https://github.com/shadcn-ui/ui/issues/2385
                          value={
                            field.value instanceof Date
                              ? field.value.toISOString().split('T')[0]
                              : field.value
                          }
                        /> */}
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="endTime"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>End Time</FormLabel>
                      <FormControl>
                        <TimePicker
                          date={field.value}
                          setDate={field.onChange}
                          {...field}
                        />

                        {/* <Input
                          type="time"
                          {...field}
                          // The "value" fix was taken from github.com
                          // Author: rafael-jordao (https://github.com/rafael-jordao)
                          // Link: https://github.com/shadcn-ui/ui/issues/2385
                          value={
                            field.value instanceof Date
                              ? field.value.toISOString().split('T')[0]
                              : field.value
                          }
                        /> */}
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>

              <Separator className="mx-auto w-3/5" />

              <FormField
                control={form.control}
                name="addressId"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Address</FormLabel>
                    <Select
                      onValueChange={(value) => field.onChange(parseInt(value))}
                      defaultValue={field.value.toString()}
                    >
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="Select an address" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        {/* {mockAddresses.map((address) => (
                          <SelectItem
                            key={address.id}
                            value={address.id.toString()}
                          >
                            {address.name}
                          </SelectItem>
                        ))} */}

                        {addresses?.data?.map((address) => (
                          <SelectItem
                            key={address.id}
                            value={address.id.toString()}
                          >
                            {address.address}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                    <FormDescription>
                      The address where the lesson will take place.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="contactDetailsId"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Contact Details (By Name)</FormLabel>
                    <Select
                      onValueChange={(value) => field.onChange(parseInt(value))}
                      defaultValue={field.value.toString()}
                    >
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="Select contact details" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        {/* {mockContactDetails.map((contact) => (
                          <SelectItem
                            key={contact.id}
                            value={contact.id.toString()}
                          >
                            {contact.name}
                          </SelectItem>
                        ))} */}

                        {contactDetails?.data?.map((contact) => (
                          <SelectItem
                            key={contact.id}
                            value={contact.id.toString()}
                          >
                            {contact.name}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                    <FormDescription>
                      The contact details for the lesson.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="paymentDetailsId"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Payment Details (By Account Number)</FormLabel>
                    <Select
                      onValueChange={(value) => field.onChange(parseInt(value))}
                      defaultValue={field.value.toString()}
                    >
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="Select payment details" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        {/* {mockPaymentDetails.map((payment) => (
                          <SelectItem
                            key={payment.id}
                            value={payment.id.toString()}
                          >
                            {payment.name}
                          </SelectItem>
                        ))} */}

                        {paymentDetails?.data?.map((payment) => (
                          <SelectItem
                            key={payment.id}
                            value={payment.id.toString()}
                          >
                            {payment?.accountNumber}

                            {/* <div className="flex flex-col justify-center gap-3">
                                <div className="flex items-center">
                                  <RectangleEllipsis className="mr-2 h-4 w-4 text-muted-foreground" />
                                  {payment?.accountNumber}
                                </div>
                              </div> */}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                    <FormDescription>
                      The payment details for the lesson.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="paymentAmount"
                render={({ field }) => (
                  <FormItem className="w-full">
                    <FormLabel>Amount</FormLabel>
                    <FormControl>
                      <Input
                        type="number"
                        step={'.01'}
                        placeholder="00.00"
                        min={0}
                        {...field}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {/* <FormField
                control={form.control}
                name="paymentAmount"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Payment Amount</FormLabel>
                    <FormControl>
                      <Input
                        type="number"
                        {...field}
                        onChange={(e) => field.onChange(e.target.valueAsNumber)}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              /> */}
            </form>
          </Form>
        </div>

        <DrawerFooter>
          <Button disabled={itemAction.isPending} form="lesson-creation-form">
            {mode === 'create' ? (
              itemAction.isPending ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  <span>Adding Lesson...</span>
                </>
              ) : (
                <>
                  <CirclePlus className="mr-1 size-4" />
                  <span>Add Lesson</span>
                </>
              )
            ) : itemAction.isPending ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                <span>Updating Lesson...</span>
              </>
            ) : (
              <>
                <Pencil className="mr-1 size-4" />
                <span>Update Lesson</span>
              </>
            )}
          </Button>
          <DrawerClose asChild>
            <Button variant="outline">Cancel</Button>
          </DrawerClose>
        </DrawerFooter>
      </DrawerContent>
    </Drawer>
  )
}
