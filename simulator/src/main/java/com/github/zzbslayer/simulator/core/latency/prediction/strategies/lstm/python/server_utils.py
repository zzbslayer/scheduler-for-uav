class Code:
    SUCCESS = 200
    BAD_REQUEST = 400

class Message:
    SUCCESS = "success"
    BAD_REQUEST = "bad request"

def make_msg(data=None, msg=Message.SUCCESS, code=Code.SUCCESS):
    return {
        "msg": msg,
        "data": data,
        "code": code,
    }

class Response:
    BAD_REQUEST = make_msg(msg=Message.BAD_REQUEST, code=Code.BAD_REQUEST)
    
    def bad_request():
        return Response.BAD_REQUEST
    def success(data=None):
        return make_msg(data, msg=Message.SUCCESS, code=Code.SUCCESS)
    def custom_response(data, msg, code):
        return make_msg(data, msg, code)
