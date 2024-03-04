import DataOps from './data_ops.js'
import {exchangeAuthorizationCodeForAccessToken, getGitHubUserData} from './github_api.js'

/**
 *
 * Event doc: https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-input-format
 * @param {Object} event - API Gateway Lambda Proxy Input Format
 *
 * Context doc: https://docs.aws.amazon.com/lambda/latest/dg/nodejs-prog-model-context.html
 * @param {Object} context
 *
 * Return doc: https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html
 * @returns {Object} object - API Gateway Lambda Proxy Output Format
 *
 */
export async function github(event) {
    if (event.queryStringParameters) {
        const {code, state, error, error_description, error_uri} = event.queryStringParameters
        const {app, host} = parseState(state)
        if (code) {
            try {
                const accessToken = await exchangeAuthorizationCodeForAccessToken(code)
                const githubUser = await getGitHubUserData(accessToken)
                await new DataOps().saveAuthenticatedUser({appName: app, github: githubUser})
                // todo create jwt
                const token = 'abcdef'
                return redirect(`https://${host}?auth84token=${token}`)
            } catch (e) {
                console.log(e.message)
            }
            return redirect(`https://${host}?auth84error`)
        } else if (error) {
            console.log('github oauth error', error, error_description, error_uri)
            return redirect(`https://${host}?auth84error`)
        }
    }
    return {
        statusCode: 400, headers: {}, body: '',
    }
}

function parseState(state) {
    if (state) {
        const [app, dev] = state.split('|')
        let host = app + '.eighty4.tech'
        if (dev === 'dev') {
            host = 'dev.' + host
        }
        return {app, host}
    } else {
        return {host: 'eighty4.tech'}
    }
}

function redirect(Location) {
    return {statusCode: 301, headers: {Location}, body: ''}
}
