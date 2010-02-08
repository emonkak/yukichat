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




var CHAT_SERVER = "yuki";


function add_message(user, message) {
  $('#section').append(
    $('<tr/>')
    .addClass($('#section tr').length % 2 == 0 ? 'even' : 'odd')
    .append($('<td/>').addClass('user').text(user))
    .append($('<td/>').addClass('message').text(message))
  );
  var targetOffset = $('#section tr').last().offset().top;
  $('html').animate({scrollTop: targetOffset}, 1000);
}


function polling() {
  $.ajax({
    cache: false,
    type: 'GET',
    url: CHAT_SERVER,
    success: function(data){
      if (!data) {
        setTimeout("polling()", 1000)
        return;
      }
      add_message(data.user, data.message);
      polling();
    },
    error: function(){
      add_message('error', 'polling() faild!');
    }
  });
}


function publish() {
  var user = $('#user').val();
  var message = $('#message').val();
  if (user === '' && message === '') return;
  $.ajax({
    cache: false,
    contentType: 'application/x-www-form-urlencoded; charset=utf-8',
    data: {user: user, message: message},
    dataType: 'text',
    type: 'POST',
    url: CHAT_SERVER,
    success: function(data){
      $('#message').val('');
    },
    error: function(){
      add_message('error', 'publish() faild!');
    }
  });
}


jQuery(function($){
  polling();
  $('#message').keypress(function(e){
    if (e.which == 13)
      publish();
  });
});




// __END__  {{{1
// vim: expandtab shiftwidth=2 softtabstop=2
// vim: foldmethod=marker
