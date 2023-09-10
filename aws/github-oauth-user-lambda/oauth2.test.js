process.env.GITHUB_OAUTH_CLIENT_ID = 'client_id'
process.env.GITHUB_OAUTH_CLIENT_SECRET = 'client_secret'
import {github} from './oauth2.js'

describe('github oauth redirect fn', () => {

    describe('redirect error', () => {

        it('when oauth errors', async () => {
            expect(await github({queryStringParameters: {error: 'bad_oauth'}})).toStrictEqual({
                statusCode: 301, headers: {Location: 'https://eighty4.tech?auth84error'}, body: '',
            })
        })
    })

    describe('returns bad request', () => {

        it('when lambda event is not an http request', async () => {
            expect(await github({})).toStrictEqual({
                statusCode: 400, headers: {}, body: '',
            })
        })
    })
})
