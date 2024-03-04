/**
 * @typedef {import('./data_ops.js').TableNames} TableNames
 */

import {CreateTableCommand, DeleteTableCommand, GetItemCommand, ListTablesCommand} from '@aws-sdk/client-dynamodb'
import DataOps from './data_ops.js'

describe('DataOps', () => {

    describe('saveAuthenticatedUser', () => {

        it('creates a new user', async () => {
            const tableNames = await createTables()
            const dataOps = new DataOps({tableNames})
            const authData = {appName: 'abacus', github: {userId: 53, email: 'franky@onett_sharks.com'}}
            await dataOps.saveAuthenticatedUser(authData)

            await new Promise(res => setTimeout(res, 1000))
            const client = DataOps.dynamodbClient()
            const eighty4UserData = await client.send(new GetItemCommand({
                Key: {github_user_id: {N: authData.github.userId + ''}},
                TableName: tableNames.githubUser,
            }))
            const githubUserData = await client.send(new GetItemCommand({
                Key: {github_user_id: {N: authData.github.userId + ''}},
                TableName: tableNames.githubUser,
            }))
            expect(eighty4UserData.Item).toEqual(githubUserData.Item)
            expect(eighty4UserData.Item.user_id.S.length).toBe(36)
            expect(parseInt(eighty4UserData.Item.login_timestamp.N, 10)).toBeGreaterThan(1681507142107)
            expect(eighty4UserData.Item.github_user_id.N).toBe('53')
            expect(eighty4UserData.Item.email.S).toBe('franky@onett_sharks.com')
        }, 10000)

        it('logs in a returning user', async () => {
            const tableNames = await createTables()
            const dataOps = new DataOps({tableNames})
            const authData = {appName: 'abacus', github: {userId: 53, email: 'franky@onett_sharks.com'}}
            await dataOps.saveAuthenticatedUser(authData)
            await new Promise(res => setTimeout(res, 2000))
            await dataOps.saveAuthenticatedUser(authData)
        }, 10000)
    })

    afterAll(async () => {
        try {
            const client = DataOps.dynamodbClient()
            const result = await client.send(new ListTablesCommand({}))
            const deletingTables = result.TableNames
                .filter(tableName => tableName.startsWith('test-eighty4-auth-'))
                .map(TableName => new DeleteTableCommand({TableName}))
                .map(command => client.send(command))
            await Promise.all(deletingTables)
        } catch (e) {
            console.log('error deleting tables:', e.message)
        }
    })
})

/**
 * @returns {Promise<TableNames>}
 */
async function createTables() {
    const client = DataOps.dynamodbClient()
    const tableNames = {
        eighty4User: tableName('eighty4-auth-users'),
        githubUser: tableName('eighty4-auth-github-users'),
    }
    await client.send(new CreateTableCommand({
        AttributeDefinitions: [
            {
                AttributeName: 'user_id',
                AttributeType: 'S',
            },
        ],
        KeySchema: [
            {
                AttributeName: 'user_id',
                KeyType: 'HASH',
            },
        ],
        ProvisionedThroughput: {
            ReadCapacityUnits: 1,
            WriteCapacityUnits: 1,
        },
        TableName: tableNames.eighty4User,
        StreamSpecification: {
            StreamEnabled: false,
        },
    }))
    await client.send(new CreateTableCommand({
        AttributeDefinitions: [
            {
                AttributeName: 'github_user_id',
                AttributeType: 'N',
            },
        ],
        KeySchema: [
            {
                AttributeName: 'github_user_id',
                KeyType: 'HASH',
            },
        ],
        ProvisionedThroughput: {
            ReadCapacityUnits: 1,
            WriteCapacityUnits: 1,
        },
        TableName: tableNames.githubUser,
        StreamSpecification: {
            StreamEnabled: false,
        },
    }))
    await new Promise(res => setTimeout(res, 7000))
    return tableNames
}

/**
 * @param {string} tableName
 * @returns {string}
 */
function tableName(tableName) {
    const letters = 'abcdefghijklmnopqrstuvwxyz'
    let suffix = ''
    for (let i = 0; i < 4; i++) {
        suffix += letters.charAt(Math.floor(Math.random() * 26))
    }
    return `test-${tableName}-${suffix}`
}
