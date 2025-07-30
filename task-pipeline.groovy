def repoUrl = 'https://github.com/galfrylich/sigmabit-task'
def gitCreds = '6b80b596-d980-4953-ab1e-8079798cd255'
def dockerCreds = '08ae2d95-b16c-4123-bdd8-5ac427619449	'

pipelineJob('build-and-push-flask') {
    displayName('Build and Push Flask')
    description('Build and push the Flask Docker image')

    parameters {
        stringParam('DOCKERHUB_CRED_ID', dockerCreds, 'DockerHub Credentials ID')
        stringParam('IMAGE_NAME', 'galfrylich/sigmabit-task', 'Flask image name')
    }

    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url(repoUrl)
                        credentials(gitCreds)
                    }
                    branches('*/main')
                }
            }
            scriptPath('flask_app.groovy')
        }
    }
}

pipelineJob('build-and-push-nginx') {
    displayName('Build and Push Nginx')
    description('Build and push the Nginx Docker image')

    parameters {
        stringParam('DOCKERHUB_CRED_ID', dockerCreds, 'DockerHub Credentials ID')
        stringParam('IMAGE_NAME', 'galfrylich/nginx-proxy', 'Nginx image name')
    }

    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url(repoUrl)
                        credentials(gitCreds)
                    }
                    branches('*/main')
                }
            }
            scriptPath('nginx_app.groovy')
        }
    }
}

pipelineJob('run-containers-and-test') {
    displayName('Run and Test Flask + Nginx')
    description('Runs both containers and verifies response')

    parameters {
        stringParam('FLASK_IMAGE', 'galfrylich/sigmabit-task', 'Flask image to run')
        stringParam('NGINX_IMAGE', 'galfrylich/nginx-proxy', 'Nginx image to run')
    }

    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url(repoUrl)
                        credentials(gitCreds)
                    }
                    branches('*/main')
                }
            }
            scriptPath('run-and-test.groovy')
        }
    }
}
