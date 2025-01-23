import { clsx, type ClassValue } from 'clsx'
import { format } from 'date-fns'
import { twMerge } from 'tailwind-merge'

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export function formatDate(date: string) {
  return format(date, 'dd MMMM yyyy')
}

export function formatTime(date?: Date) {
  if (!date) return ''

  return format(date, 'hh:mm:ss a')
}
