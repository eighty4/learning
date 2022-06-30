export interface Activity {
    title: String
    location?: Location
    when: Scheduling
}

export interface Coord {
    lat: number
    lon: number
}

export interface Location {
    coord: Coord
}

export type Scheduling = ExplicitScheduling | WeeklyScheduling | MonthlyScheduling

export interface ExplicitScheduling {
    explicitly: Date
}

export type Day = 'MON' | 'TUE' | 'WED' | 'THU' | 'FRI' | 'SAT' | 'SUN'

export interface WeeklyScheduling {
    weekly: Day
}

export interface MonthlyScheduling {
    monthly: number
}
