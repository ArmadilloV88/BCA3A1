'use client'

import { ColumnDef } from '@tanstack/react-table'
import { Button } from '@/components/shadcn-ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/shadcn-ui/dropdown-menu'
import { MoreHorizontal, Pencil, Trash2 } from 'lucide-react'
import { DataTable } from '../shadcn-ui/data-table'
import { Address } from '@/models/address'
import useAddresses from '@/hooks/useAddresses'
import { DataTableColumnHeader } from '../shadcn-ui/data-table-column-header'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/shadcn-ui/card'
import { useState } from 'react'
import { Separator } from '../shadcn-ui/separator'
import AddressFormDrawer from './address-form-drawer'
import DeletionDialog from './deletion-dialog'
import UserFormDrawer from './user-form-drawer'

export default function AddressesTable() {
  const [isTableFormOpen, setIsTableFormOpen] = useState(false)
  const { addresses, addressCreation, addressUpdation, addressDeletion } =
    useAddresses()

  const columns: ColumnDef<Address>[] = [
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
      id: 'Address',
      accessorKey: 'address',
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Address" />
      ),
    },
    {
      id: 'actions',
      enableHiding: false,
      cell: ({ row }) => {
        const [formIsOpen, setFormIsOpen] = useState(false)
        const [deletionDialogIsOpen, setDeletionDialogIsOpen] = useState(false)

        return (
          <div className="flex items-center justify-center">
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" className="h-8 w-8 p-0">
                  <span className="sr-only">Open menu</span>
                  <MoreHorizontal className="h-4 w-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                {/* <DropdownMenuItem>View details</DropdownMenuItem>
                <DropdownMenuItem>Edit address</DropdownMenuItem>
                <DropdownMenuItem>Delete address</DropdownMenuItem> */}

                <DropdownMenuItem onClick={() => setFormIsOpen(() => true)}>
                  <Pencil className="mr-1 size-4" />
                  Edit Address
                </DropdownMenuItem>
                <DropdownMenuItem
                  onClick={() => setDeletionDialogIsOpen(() => true)}
                >
                  <Trash2 className="mr-1 size-4" />
                  Delete Address
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>

            <DeletionDialog
              nameOfData="address"
              dataId={row.original.id.toString()}
              dataIdToDelete={row.original.id}
              dialogTrigger={<p></p>}
              isOpen={deletionDialogIsOpen}
              onOpenChanged={setDeletionDialogIsOpen}
              itemDeletion={addressDeletion}
            />

            <AddressFormDrawer
              mode="update"
              addressToUpdate={row.original}
              trigger={<p></p>}
              isOpen={formIsOpen}
              onOpenChanged={(newValue) => setFormIsOpen(() => newValue)}
              itemAction={addressUpdation}
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
          <CardTitle>Addresses</CardTitle>
          <CardDescription>Manage all of your addresses here</CardDescription>
        </CardHeader>
        <CardContent>
          <Separator className="mx-auto my-2 w-1/4" />

          <div className="container mx-auto">
            <DataTable
              columns={columns}
              data={typeof addresses.data === 'undefined' ? [] : addresses.data}
              isLoading={addresses.isLoading}
              isError={addresses.isError}
              error={addresses.error}
              addItemForm={
                <AddressFormDrawer
                  itemAction={addressCreation}
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
