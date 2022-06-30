import {createElasticSearchClient} from '../src/ElasticSearch'
import ActivityQueries from '../src/ActivityQueries'

import type {Client} from '@elastic/elasticsearch'
import type {Scheduling} from '../src/Activity'
import {ActivityIndex} from '../src/ActivityIndex'

describe('ActivityQueries', () => {

    const index = 'activities'

    let es: Client

    let id: number = 0

    const createActivity = (title: string, when: Scheduling) => es.create({
        index,
        id: `${id++}`,
        document: createDocument(title, when),
        refresh: 'true',
    })

    const createDocument = (title: string, when: Scheduling) => {
        const scheduling = (when as any)
        if (scheduling.weekly) {
            return {title, weekly: scheduling.weekly}
        } else if (scheduling.monthly) {
            return {title, monthly: scheduling.monthly}
        } else if (scheduling.explicitly) {
            return {title, explicitly: scheduling.explicitly}
        } else {
            throw new Error()
        }
    }

    const waitForIndex = () => new Promise((res) => setTimeout(res, 10))

    beforeEach(async () => {
        es = createElasticSearchClient()
        try {
            await es.indices.delete({index})
        } catch (e) {
        }
        await new ActivityIndex(es, index).initializeIndex()
        await createActivity('Bluegrass Brunch', {weekly: 'SAT'})
        await createActivity('Soul Message', {weekly: 'SUN'})
        await createActivity('Hoyle Brothers Honky Tonk Happy Hour', {weekly: 'MON'})
        await waitForIndex()
    })

    it('match weekly scheduled activity', async () => {
        const activities = await new ActivityQueries(es, index).findActivitiesByDay('SUN')
        expect(activities).toHaveLength(1)
        expect(activities[0]).toStrictEqual({
            title: 'Soul Message',
            when: {weekly: 'SUN'},
        })
    })
})
