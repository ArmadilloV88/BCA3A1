import a from 'axios'

const axios = a.create({
  baseURL: process.env.NEXT_PUBLIC_DB_URL,
  headers: {
    apiKey: process.env.NEXT_PUBLIC_API_KEY,
  },
})

export default axios
