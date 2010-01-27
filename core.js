// yukichat, the chat servlet that used comet
// Version: 0.0.0
// Copyright (C) 2010 emon <http://github.com/emonkak>
// License: MIT license  {{{
//     Permission is hereby granted, free of charge, to any person
//     obtaining a copy of this software and associated documentation
//     files (the "Software"), to deal in the Software without
//     restriction, including without limitation the rights to use,
//     copy, modify, merge, publish, distribute, sublicense, and/or
//     sell copies of the Software, and to permit persons to whom the
//     Software is furnished to do so, subject to the following
//     conditions:
//
//     The above copyright notice and this permission notice shall be
//     included in all copies or substantial portions of the Software.
//
//     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
//     EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
//     OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
//     NONINFRINGEMENT.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
//     HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
//     WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
//     FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
//     OTHER DEALINGS IN THE SOFTWARE.
// }}}

const CHAT_SERVER = "yuki"


function connect_yuki() {
  $.get(CHAT_SERVER, function(data){
    $('#section').prepend($('<p />').text(data))
    connect_yuki();
  });
}


function post_message(user, message) {
  $.post(CHAT_SERVER, 'user=' + user + '&message=' + message);
}


jQuery(function($){
  connect_yuki();
  $('#message').keypress(function(e){
    if (e.which != 13) return;
    var user = $('#user').val();
    var message = $('#message').val();
    $('#message').val('');
    post_message(user, message);
    return;
  });
  $('#post').click(function(){
    var user = $('#user').val();
    var message = $('#message').val();
    post_message(user, message);
  });
});




// __END__  {{{1
// vim: expandtab shiftwidth=2 softtabstop=2
// vim: foldmethod=marker
