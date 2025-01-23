'use client'

import { ColumnDef } from '@tanstack/react-table'
import { Button } from '@/components/shadcn-ui/button'
import { DataTable } from '../shadcn-ui/data-table'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/shadcn-ui/dropdown-menu'
import { MoreHorizontal, Pencil, Trash2 } from 'lucide-react'
import { useState } from 'react'
// import { User } from '@/models/user'
import useUsers from '@/hooks/useUsers'
import { DataTableColumnHeader } from '../shadcn-ui/data-table-column-header'

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/shadcn-ui/card'

import { Separator } from '../shadcn-ui/separator'
import UserFormDrawer from './user-form-drawer'
import DeletionDialog from './deletion-dialog'
import { Badge } from '../shadcn-ui/badge'
import { cn } from '@/lib/utils'
import { useAuth, useUser } from '@clerk/nextjs'
import { User } from '@clerk/nextjs/server'
import { createUser } from '@/server/actions'

// import { Drawer } from 'vaul'

export default function UsersTable() {
  const [isTableFormOpen, setIsTableFormOpen] = useState(false)
  // const { getToken, userId } = useAuth()
  // const { user } = useUser()

  const { users, userCreation, userUpdation, userDeletion } = useUsers()

  const columns: ColumnDef<User>[] = [
    {
      id: 'Role',
      accessorKey: 'publicMetadata.userRole.role',
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Role" />
      ),
    },
    {
      id: 'Name',
      accessorKey: 'fullName',
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Name" />
      ),
      cell: ({ row }) => (
        <div>
          {row.original.firstName} {row.original.lastName}
        </div>
      ),
    },
    {
      id: 'Email',
      accessorKey: 'emailAddresses[0].emailAddress',
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Email" />
      ),
      cell: ({ row }) => (
        <div>{row.original.emailAddresses[0]?.emailAddress}</div>
      ),
    },
    {
      id: 'Phone',
      accessorKey: 'publicMetadata.phoneNumber',
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Phone" />
      ),
    },
    // {
    //   id: 'Age',
    //   accessorKey: 'age',
    //   // The accessor function is used to convert the data to string to enable better filtering
    //   accessorFn: (row) => `${row.age.toString()}`,
    //   header: ({ column }) => (
    //     <DataTableColumnHeader column={column} title="Age" />
    //   ),
    // },
    {
      id: 'Skill Level',
      accessorKey: 'skillLevel',
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Skill Level" />
      ),
      cell: ({ row }) => {
        const skillLevel = row.original?.publicMetadata?.skillLevel?.level

        return (
          skillLevel != undefined && (
            <Badge
              variant="outline"
              className={cn(
                skillLevel === 'Beginner' &&
                  'border-orange-700 text-orange-700',
                skillLevel === 'Intermediate' &&
                  'border-slate-500 text-slate-500',
                skillLevel === 'Advanced' && 'border-amber-500 text-amber-500'
              )}
            >
              {skillLevel}
            </Badge>
          )
        )
      },
    },
    {
      id: 'actions',
      cell: ({ row }) => {
        const [formIsOpen, setFormIsOpen] = useState(false)
        const [deletionDialogIsOpen, setDeletionDialogIsOpen] = useState(false)

        return (
          <div className="flex items-center justify-center">
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" size="icon">
                  <MoreHorizontal className="h-4 w-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent>
                {/* <DropdownMenuItem> */}
                <DropdownMenuItem onClick={() => setFormIsOpen(() => true)}>
                  <Pencil className="mr-1 size-4" />
                  Edit User
                </DropdownMenuItem>
                {/* </DropdownMenuItem> */}
                <DropdownMenuItem
                  onClick={() => setDeletionDialogIsOpen(() => true)}
                >
                  <Trash2 className="mr-1 size-4" />
                  Delete User
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>

            <DeletionDialog
              nameOfData="user"
              dataId={row.original.fullName}
              dataIdToDelete={row.original.id}
              dialogTrigger={<p></p>}
              isOpen={deletionDialogIsOpen}
              onOpenChanged={setDeletionDialogIsOpen}
              itemDeletion={userDeletion}
            />

            <UserFormDrawer
              mode="update"
              userToUpdate={row.original}
              trigger={<p></p>}
              isOpen={formIsOpen}
              onOpenChanged={(newValue) => setFormIsOpen(() => newValue)}
              itemAction={userUpdation}
            />
          </div>
        )
      },
    },
  ]

  return (
    <div className="py-10">
      <Card>
        <CardHeader className="flex w-full items-center justify-center">
          <CardTitle>Users</CardTitle>
          <CardDescription>Manage all of your users here</CardDescription>
        </CardHeader>
        <CardContent>
          <Separator className="mx-auto my-2 w-1/4" />

          <div className="container mx-auto">
            <DataTable
              columns={columns}
              data={typeof users.data === 'undefined' ? [] : users.data}
              isLoading={users.isLoading}
              isError={users.isError}
              error={users.error}
              addItemForm={
                <UserFormDrawer
                  itemAction={userCreation}
                  isOpen={isTableFormOpen}
                  onOpenChanged={(newValue) =>
                    setIsTableFormOpen(() => newValue)
                  }
                />
              }
            />
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
