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
import { PaymentDetails } from '@/models/payment-details'
import usePaymentDetails from '@/hooks/usePaymentDetails'
import { DataTableColumnHeader } from '../shadcn-ui/data-table-column-header'

import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/shadcn-ui/card'
import { Separator } from '../shadcn-ui/separator'
import UserFormDrawer from './user-form-drawer'
import PaymentDetailsFormDrawer from './payment-details-form-drawer'
import DeletionDialog from './deletion-dialog'

//DUMMY DATA THAT NEEDS TO CHANGE
// const initialPayments: PaymentDetails[] = [
//   { id: '1', user: 'Alice', amount: 100, date: '2024-10-20', status: 'Paid' },
//   { id: '2', user: 'Bob', amount: 200, date: '2024-10-21', status: 'Pending' },
// ]

export default function PaymentDetailsTable() {
  const [isTableFormOpen, setIsTableFormOpen] = useState(false)
  const {
    paymentDetails,
    paymentDetailsCreation,
    paymentDetailsUpdation,
    paymentDetailsDeletion,
  } = usePaymentDetails()

  const columns: ColumnDef<PaymentDetails>[] = [
    // { accessorKey: 'user', header: 'User' },
    // { accessorKey: 'amount', header: 'Amount' },
    // { accessorKey: 'date', header: 'Date' },
    // { accessorKey: 'status', header: 'Status' },
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
      id: 'Bank',
      accessorKey: 'bank',
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Bank" />
      ),
    },
    {
      id: 'Branch',
      accessorKey: 'branch',
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Branch" />
      ),
    },
    {
      id: 'Branch Code',
      accessorKey: 'branchCode',
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Branch Code" />
      ),
    },
    {
      id: 'Account Number',
      accessorKey: 'accountNumber',
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Account Number" />
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
                <DropdownMenuItem>Edit Payment</DropdownMenuItem> */}

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
              nameOfData="payment details"
              dataId={row.original.id.toString()}
              dataIdToDelete={row.original.id}
              dialogTrigger={<p></p>}
              isOpen={deletionDialogIsOpen}
              onOpenChanged={setDeletionDialogIsOpen}
              itemDeletion={paymentDetailsDeletion}
            />

            <PaymentDetailsFormDrawer
              mode="update"
              paymentDetailsToUpdate={row.original}
              trigger={<p></p>}
              isOpen={formIsOpen}
              onOpenChanged={(newValue) => setFormIsOpen(() => newValue)}
              itemAction={paymentDetailsUpdation}
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
          <CardTitle>Payment Details</CardTitle>
          <CardDescription>
            Manage all of your payments details here
          </CardDescription>
        </CardHeader>
        <CardContent>
          <Separator className="mx-auto my-2 w-1/4" />

          <div className="container mx-auto">
            <DataTable
              columns={columns}
              data={
                typeof paymentDetails.data === 'undefined'
                  ? []
                  : paymentDetails.data
              }
              isLoading={paymentDetails.isLoading}
              isError={paymentDetails.isError}
              error={paymentDetails.error}
              addItemForm={
                <PaymentDetailsFormDrawer
                  itemAction={paymentDetailsCreation}
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
