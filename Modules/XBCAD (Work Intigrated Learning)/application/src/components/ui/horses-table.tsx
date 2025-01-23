'use client'

import { ColumnDef } from '@tanstack/react-table'
import { Button } from '@/components/shadcn-ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/shadcn-ui/dropdown-menu'
import { ChevronDown, MoreHorizontal, Pencil, Trash2 } from 'lucide-react'
import { DataTable } from '../shadcn-ui/data-table'
import { Horse } from '@/models/horse'
import useHorses from '@/hooks/useHorses'
import { DataTableColumnHeader } from '../shadcn-ui/data-table-column-header'

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/shadcn-ui/card'
import { Separator } from '../shadcn-ui/separator'
import { useState } from 'react'
import HorseFormDrawer from './horse-form-drawer'
import DeletionDialog from './deletion-dialog'
import { Badge } from '../shadcn-ui/badge'
import { cn } from '@/lib/utils'

export default function HorsesTable() {
  const [isTableFormOpen, setIsTableFormOpen] = useState(false)
  const { horses, horseCreation, horseUpdation, horseDeletion } = useHorses()

  // const handleSkillChange = (id: string, newSkill: Horse['skillLevel']) => {
  //   setData((prevData) =>
  //     prevData.map((horse) =>
  //       horse.id === id ? { ...horse, skillLevel: newSkill } : horse
  //     )
  //   )
  // }

  const columns: ColumnDef<Horse>[] = [
    {
      id: 'Created By',
      accessorKey: 'user.name',
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Created By" />
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
      id: 'Breed',
      accessorKey: 'breed',
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Breed" />
      ),
    },
    {
      id: 'Age',
      accessorKey: 'age',
      // The accessor function is used to convert the data to string to enable better filtering
      accessorFn: (row) => `${row.age.toString()}`,
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Age" />
      ),
    },
    {
      id: 'Years Worked',
      accessorKey: 'yearsWorked',
      // The accessor function is used to convert the data to string to enable better filtering
      accessorFn: (row) => `${row.yearsWorked.toString()}`,
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Years Worked" />
      ),
    },
    {
      id: 'Skill Level',
      accessorKey: 'skillLevel',
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Skill Level" />
      ),
      cell: ({ row }) => {
        const skillLevel = row.original.skillLevel?.level
        return (
          skillLevel !== undefined && (
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
      // enableHiding: false,
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
                {/* <DropdownMenuItem
                  onClick={() => navigator.clipboard.writeText(horse.id)}
                >
                  Copy horse ID
                </DropdownMenuItem>
                <DropdownMenuItem>View details</DropdownMenuItem>
                <DropdownMenuItem>Contact owner</DropdownMenuItem> */}
                <DropdownMenuItem onClick={() => setFormIsOpen(() => true)}>
                  <Pencil className="mr-1 size-4" />
                  Edit Horse
                </DropdownMenuItem>
                <DropdownMenuItem
                  onClick={() => setDeletionDialogIsOpen(() => true)}
                >
                  <Trash2 className="mr-1 size-4" />
                  Delete Horse
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>

            <DeletionDialog
              nameOfData="horse"
              dataId={row.original.name}
              dataIdToDelete={row.original.id}
              dialogTrigger={<p></p>}
              isOpen={deletionDialogIsOpen}
              onOpenChanged={setDeletionDialogIsOpen}
              itemDeletion={horseDeletion}
            />

            <HorseFormDrawer
              mode="update"
              horseToUpdate={row.original}
              trigger={<p></p>}
              isOpen={formIsOpen}
              onOpenChanged={(newValue) => setFormIsOpen(() => newValue)}
              itemAction={horseUpdation}
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
          <CardTitle>Horses</CardTitle>
          <CardDescription>
            Manage all of your training horses here
          </CardDescription>
        </CardHeader>
        <CardContent>
          <Separator className="mx-auto my-2 w-1/4" />

          <div className="container mx-auto">
            <DataTable
              columns={columns}
              data={typeof horses.data === 'undefined' ? [] : horses.data}
              isLoading={horses.isLoading}
              isError={horses.isError}
              error={horses.error}
              addItemForm={
                <HorseFormDrawer
                  itemAction={horseCreation}
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
