// Example starter JavaScript for disabling form submissions if there are invalid fields
(function () {
  'use strict'

  // Fetch all the forms we want to apply custom Bootstrap validation styles to
  // in my customised page, there is only one form, no need to loop them
  var forms = document.querySelectorAll('.needs-validation')
  let child = forms[0].children;
  console.log(child);
  child[3].addEventListener('click', function (event) {
    if (!forms[0].checkValidity()) {
      event.preventDefault()
      event.stopPropagation()
    }

    forms[0].classList.add('was-validated')
  }, false)
})()
