import { User } from '@clerk/nextjs/server'
import { Address } from './address'
import { ContactDetails } from './contact-details'
import { PaymentDetails } from './payment-details'
import { SkillLevel } from './skill-level'

export interface Lesson {
  id: number
  title: string
  description: string
  date: Date
  // Fix start and end times types
  startTime: Date
  endTime: Date

  // Add more payment details
  paymentAmount: number
  skillLevelId: number
  skillLevel?: SkillLevel

  addressId: number
  address?: Address

  contactDetailsId: number
  contactDetails?: ContactDetails

  paymentDetailsId: number
  paymentDetails?: PaymentDetails

  // Fix coach id number
  coachId: string

  totalSlots: number
  // slotsTaken is the bookingIds.length
  bookingIds: number[]
}

export interface LessonBooking {
  id?: number
  lessonId: number
  lesson?: Lesson
  userId: string
  // horseId?: number

  confirmationPending: boolean
  paymentConfirmed: boolean

  usingPersonalHorse: boolean

  messageForCoach?: string
  proofOfPayment: string

  // lesson?: Lesson
  // horse?: Horse
  // user?: User
}
