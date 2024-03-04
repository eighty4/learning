/**
 * @typedef {Object} GitHubUserData
 * @property {string} GitHubUserData.userId
 * @property {Object} GitHubUserData.email
 */

export async function exchangeAuthorizationCodeForAccessToken(code) {
    const {clientId, clientSecret} = getGitHubOAuthClientCredentials()
    const q = `code=${code}&client_id=${clientId}&client_secret=${clientSecret}`
    const response = await fetch(`https://github.com/login/oauth/access_token?${q}`, {
        method: 'POST', headers: {
            Accept: 'application/json',
        },
    })
    if (response.status === 200) {
        const {access_token, scope} = await response.json()
        console.log(scope)
        // todo error if user:email scope not present
        console.log(access_token)
        return access_token
    } else {
        console.log('DEBUG https://github.com/login/oauth/access_token', await response.text())
        throw new Error(`github oauth exchanging code for access token returned ${response.status} status`)
    }
}

function getGitHubOAuthClientCredentials() {
    const {GITHUB_OAUTH_CLIENT_ID: clientId, GITHUB_OAUTH_CLIENT_SECRET: clientSecret} = process.env
    if (!clientId || !clientSecret) {
        throw new Error('env not configured')
    } else {
        return {clientId, clientSecret}
    }
}

/**
 * @param {string} accessToken
 * @returns {Promise<GitHubUserData>}
 */
export async function getGitHubUserData(accessToken) {
    try {
        const [userId, email] = await Promise.all([
            getGitHubUserId(accessToken),
            getGitHubEmailAddress(accessToken),
        ])
        return {userId, email}
    } catch (e) {
        console.log(Object.keys(e))
        console.log(JSON.stringify(e))
        throw new Error('ASDGASDG')
    }
}

async function getGitHubUserId(accessToken) {
    const url = 'https://api.github.com/user'
    const response = await fetch(url, {
        headers: {
            Accept: 'application/json',
            'Accept-Encoding': 'gzip, deflate',
            Authorization: 'Bearer ' + accessToken,
        },
    })
    if (response.status === 200) {
        const user = await response.json()
        return user.id
    } else {
        console.log('DEBUG https://api.github.com/user', await response.text())
        throw new Error(`github get user returned ${response.status} status`)
    }
}

async function getGitHubEmailAddress(accessToken) {
    const response = await fetch('https://api.github.com/user/emails', {
        headers: {
            Accept: 'application/json',
            'Accept-Encoding': 'gzip, deflate',
            Authorization: 'Bearer ' + accessToken,
        },
    })
    if (response.status === 200) {
        const emails = await response.json()
        const primary = emails.find(emailData => emailData.primary)
        if (primary) {
            return primary.email
        }
    } else {
        console.log('DEBUG https://api.github.com/user/emails', await response.text())
        throw new Error(`github get user emails returned ${response.status} status`)
    }
}
