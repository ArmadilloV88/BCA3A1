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
import { ContactDetails } from '@/models/contact-details'
import useContactDetails from '@/hooks/useContactDetails'
import { DataTableColumnHeader } from '../shadcn-ui/data-table-column-header'

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/shadcn-ui/card'
import { Separator } from '../shadcn-ui/separator'
import ContactDetailsFormDrawer from './contact-details-form-drawer'
import DeletionDialog from './deletion-dialog'

export default function ContactDetailsTable() {
  const [isTableFormOpen, setIsTableFormOpen] = useState(false)
  const {
    contactDetails,
    contactDetailsCreation,
    contactDetailsUpdation,
    contactDetailsDeletion,
  } = useContactDetails()

  const columns: ColumnDef<ContactDetails>[] = [
    {
      id: 'ID',
      accessorKey: 'id',
      // The accessor function is used to convert the data to string to enable better filtering
      accessorFn: (row) => `${row.id.toString()}`,
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="ID" />
      ),
    },
    {
      id: 'Name',
      accessorKey: 'name',
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Name" />
      ),
    },
    {
      id: 'Phone',
      accessorKey: 'phone',
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Phone" />
      ),
    },
    {
      id: 'Email',
      accessorKey: 'email',
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Email" />
      ),
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
                <Button variant="ghost" className="h-8 w-8 p-0">
                  <MoreHorizontal className="h-4 w-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent>
                {/* <DropdownMenuItem>View Details</DropdownMenuItem>
                <DropdownMenuItem>Edit</DropdownMenuItem> */}

                <DropdownMenuItem onClick={() => setFormIsOpen(() => true)}>
                  <Pencil className="mr-1 size-4" />
                  Edit Details
                </DropdownMenuItem>
                <DropdownMenuItem
                  onClick={() => setDeletionDialogIsOpen(() => true)}
                >
                  <Trash2 className="mr-1 size-4" />
                  Delete Details
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>

            <DeletionDialog
              nameOfData="contact details"
              dataId={row.original.id}
              dataIdToDelete={row.original.id}
              dialogTrigger={<p></p>}
              isOpen={deletionDialogIsOpen}
              onOpenChanged={setDeletionDialogIsOpen}
              itemDeletion={contactDetailsDeletion}
            />

            <ContactDetailsFormDrawer
              mode="update"
              contactDetailsToUpdate={row.original}
              trigger={<p></p>}
              isOpen={formIsOpen}
              onOpenChanged={(newValue) => setFormIsOpen(() => newValue)}
              itemAction={contactDetailsUpdation}
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
          <CardTitle>Contact Details</CardTitle>
          <CardDescription>
            Manage all of your contact details here
          </CardDescription>
        </CardHeader>
        <CardContent>
          <Separator className="mx-auto my-2 w-1/4" />

          <div className="container mx-auto">
            <DataTable
              columns={columns}
              data={
                typeof contactDetails.data === 'undefined'
                  ? []
                  : contactDetails.data
              }
              isLoading={contactDetails.isLoading}
              isError={contactDetails.isError}
              error={contactDetails.error}
              addItemForm={
                <ContactDetailsFormDrawer
                  itemAction={contactDetailsCreation}
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
