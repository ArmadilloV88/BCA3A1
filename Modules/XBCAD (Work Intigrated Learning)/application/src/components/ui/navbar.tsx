'use client'

import { cn } from '@/lib/utils'
import { BookCheck, DotIcon, UserCog } from 'lucide-react'
import Link from 'next/link'
import { usePathname, useRouter } from 'next/navigation'
import { Button } from '../shadcn-ui/button'
import { UserButton, UserProfile, useUser } from '@clerk/nextjs'
import { ThemeToggle } from './theme-switcher'

const routes = [
  {
    link: '/lesson-booking',
    name: 'Lessons',
    icon: <BookCheck className="size-4" />,
    placeDivider: true,
  },
  {
    link: '/admin',
    name: 'Admin',
    icon: <UserCog className="size-4" />,
  },
]

export default function Navbar() {
  const path = usePathname()
  const { user } = useUser()
  const userData = user?.publicMetadata
  const isAdmin =
    userData?.userRole.role === 'Admin' && userData?.userRole.id === 1

  return (
    <div className="flex w-full items-center justify-between gap-5">
      <div className="ml-auto flex items-center justify-center gap-5">
        {/* {routes.map((route) => (
        <Button
          variant={path.endsWith(route.link) ? 'default' : 'ghost'}
          size="sm"
          key={route.name}
          asChild
        >
          <Link href={route.link} className={cn('flex items-center gap-2')}>
            {route.icon}
            {route.name}
          </Link>
        </Button>
      ))} */}

        {isAdmin && (
          <Button
            variant={path.endsWith('/lesson-booking') ? 'default' : 'ghost'}
            size="sm"
            asChild
          >
            <Link
              href={'/lesson-booking'}
              className={cn('flex items-center gap-2')}
            >
              <BookCheck className="size-4" />
              Lessons
            </Link>
          </Button>
        )}

        <UserButton
          appearance={{
            elements: {
              // userButtonPopoverRootBox: 'ml-20', // translate-x-1/2 ml-[150px] ml-[calc(150px)]
              // userButtonPopoverCard: 'ml-[164px]', FIX THIS
              popoverBox: '',
              userButtonPopover: '',

              // open: 'static',
              // rootBox: 'flex static bg-blue-500',
              // userButtonRoot: 'flex static bg-blue-500',
              // userButtonPopoverMain: 'flex  bg-orange-500',
              // popoverBox: 'bg-rose-500',
              userButtonAvatarBox: 'w-10 h-10', // Custom width and height
              // userButtonPopoverCard: 'bg-blue-100', // Custom background for the popover card
              // userButtonPopoverActionButton: 'text-red-600', // Custom text color for action buttons
            },
          }}
        />

        {isAdmin && (
          <Button
            variant={path.endsWith('/admin') ? 'default' : 'ghost'}
            size="sm"
            asChild
          >
            <Link href={'/admin'} className={cn('flex items-center gap-2')}>
              <UserCog className="size-4" />
              Admin
            </Link>
          </Button>
        )}
      </div>

      <div className="ml-auto">
        <ThemeToggle />
      </div>
    </div>
  )
}
