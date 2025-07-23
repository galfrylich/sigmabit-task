from flask import Flask, jsonify
import docker

app = Flask(__name__)
client = docker.DockerClient(base_url='unix://var/run/docker.sock')

@app.route('/')
def list_containers():
    containers = client.containers.list()
    result = []
    for container in containers:
        result.append({
            'id': container.short_id,
            'name': container.name,
            'image': container.image.tags,
            'status': container.status
        })
    return jsonify(result)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
