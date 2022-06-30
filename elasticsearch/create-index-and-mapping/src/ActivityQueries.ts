import type {Client} from '@elastic/elasticsearch'
import type {Activity, Day} from './Activity'
import type {SearchHit} from '@elastic/elasticsearch/lib/api/types'

export default class ActivityQueries {

    constructor(private readonly es: Client,
                private readonly index: string = 'activities') {
    }

    async findActivitiesByDay(day: Day): Promise<Array<Activity>> {
        const {index} = this
        const query = {
            match: {weekly: day},
        }
        const result = await this.es.search({index, query})
        const hits = result.hits.hits.map(ActivityQueries.mapDocumentToObject)
        return Promise.resolve(hits)
    }

    private static mapDocumentToObject(doc: SearchHit<any>) {
        return {
            title: doc._source.title,
            when: {weekly: doc._source.weekly},
        }
    }
}
