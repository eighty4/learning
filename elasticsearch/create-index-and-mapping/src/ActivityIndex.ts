import {IndexInitializer} from './ElasticSearch'
import {Client} from '@elastic/elasticsearch'

export class ActivityIndex extends IndexInitializer {
    constructor(es: Client,
                index: string) {
        super(es, index, {
            title: {type: 'text'},
            weekly: {type: 'keyword'},
        })
    }
}
