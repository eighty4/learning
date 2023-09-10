/**
 * @typedef {Object} TableNames
 * @property {string} eighty4User
 * @property {string} githubUser
 *
 * @typedef {Object} DataOpsConfig
 * @property {TableNames} tableNames
 *
 * @typedef {Object} GitHubUserData
 * @property {number} userId
 * @property {string} email
 *
 * @typedef {Object} AuthenticationData
 * @property {string} appName
 * @property {GitHubUserData} [github]
 *
 * @typedef {Object} Eighty4UserData
 * @property {string} userId
 * @property {string} email
 */

import {BatchWriteItemCommand, DynamoDBClient, GetItemCommand} from '@aws-sdk/client-dynamodb'
import {v4 as createV4Uuid} from 'uuid'

export default class DataOps {

    static dynamodbClient() {
        return new DynamoDBClient({region: 'us-east-2'})
    }

    /**
     * @returns {string}
     */
    static uuid() {
        return createV4Uuid()
    }

    #client
    #config

    /**
     * @param {DataOpsConfig?} config
     */
    constructor(config) {
        this.#client = DataOps.dynamodbClient()
        this.#config = config || {
            tableNames: {
                eighty4User: 'eighty4-auth-users',
                githubUser: 'eighty4-auth-github-users',
            },
        }
    }

    /**
     * @param {AuthenticationData} authData
     * @returns {Promise<Eighty4UserData | undefined>}
     */
    async saveAuthenticatedUser(authData) {
        const userData = await this.#lookupAuthenticatedUser(authData)
        if (!userData) {
            const userId = DataOps.uuid()
            await this.#createGitHubAuthenticatedUser(userId, authData)
        }
    }

    /**
     * @param {AuthenticationData} authData
     * @returns {Promise<Eighty4UserData | undefined>}
     */
    async #lookupAuthenticatedUser(authData) {
        if (!authData.github) {
            throw new Error(`authed user data for app ${authData.appName} does not`)
        }
        const githubUserData = await this.#client.send(new GetItemCommand({
            Key: {github_user_id: {N: authData.github.userId + ''}},
            TableName: this.#config.tableNames.githubUser,
        }))
        if (githubUserData.Item) {
            return {userId: githubUserData.Item.user_id.S, email: authData.email}
        }
    }

    /**
     * @param {string} userId
     * @param {AuthenticationData} authData
     * @returns {Promise<void>}
     */
    async #createGitHubAuthenticatedUser(userId, authData) {
        const loginTimestamp = Date.now()
        const request = {
            PutRequest: {
                Item: {
                    github_user_id: {N: authData.github.userId + ''},
                    login_timestamp: {N: loginTimestamp + ''},
                    email: {S: authData.github.email},
                    user_id: {S: userId},
                },
            },
        }
        await this.#client.send(new BatchWriteItemCommand({
            RequestItems: {
                [this.#config.tableNames.eighty4User]: [request],
                [this.#config.tableNames.githubUser]: [request],
            },
        }))
    }
}
