import {Client} from '@elastic/elasticsearch'
import type {MappingProperty, PropertyName} from '@elastic/elasticsearch/lib/api/types'

export const createElasticSearchClient = () => new Client({node: 'http://localhost:9200'})

export class IndexInitializer {

    constructor(private readonly es: Client,
                private readonly index: string,
                private readonly mapping: Record<PropertyName, MappingProperty>) {
    }

    async initializeIndex() {
        const {index, mapping: properties} = this
        const {acknowledged: createIndexAck} = await this.es.indices.create({index})
        if (!createIndexAck) {
            throw new Error('did not create index ' + index)
        }
        const {acknowledged: putMappingAck} = await this.es.indices.putMapping({index, properties})
        if (!putMappingAck) {
            throw new Error('did not put mapping for index ' + index)
        }
    }
}
