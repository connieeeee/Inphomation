<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link href="css/bootstrap.css" rel="stylesheet">
<link href="css/default.css" rel="stylesheet">
</head>
<body>
	<form method="post" enctype="multipart/form-data">
		<div>
			<label for="image_uploads">Choose images to upload (PNG, JPG)</label>
			<input type="file" id="image_uploads" name="image_uploads"
				accept=".jpg, .jpeg, .png" multiple onchange="updateImageDisplay()">
		</div>
		<div class="preview">
			<p>No files currently selected for upload</p>
		</div>
		<div>
			<button>Submit</button>
		</div>
	</form>
	<script type="text/javascript">
		// upload file GPSLongitude/GPSLongitudeRef
		
		var input = document.querySelector('input');
		var preview = document.querySelector('.preview');
		input.style.opacity = 0;
		
		function returnFileSize(number) {
			  if(number < 1024) {
			    return number + 'bytes';
			  } else if(number >= 1024 && number < 1048576) {
			    return (number/1024).toFixed(1) + 'KB';
			  } else if(number >= 1048576) {
			    return (number/1048576).toFixed(1) + 'MB';
			  }
			}
		
		const fileTypes = [
			  "image/apng",
			  "image/bmp",
			  "image/gif",
			  "image/jpeg",
			  "image/pjpeg",
			  "image/png",
			  "image/svg+xml",
			  "image/tiff",
			  "image/webp",
			  "image/x-icon"
			];

			function validFileType(file) {
			  return fileTypes.includes(file.type);
			}
		function updateImageDisplay() {
			  while(preview.firstChild) {
			    preview.removeChild(preview.firstChild);
			  }

			  const curFiles = input.files;
			  if(curFiles.length === 0) {
			    const para = document.createElement('p');
			    para.textContent = 'No files currently selected for upload';
			    preview.appendChild(para);
			  } else {
			    const list = document.createElement('ol');
			    preview.appendChild(list);

			    for(const file of curFiles) {
			      const listItem = document.createElement('li');
			      const para = document.createElement('p');
			      if(validFileType(file)) {
			        para.textContent = `File name ${file.name}, file size ${returnFileSize(file.size)}.`;
			        const image = document.createElement('img');
			        image.src = URL.createObjectURL(file);

			        listItem.appendChild(image);
			        listItem.appendChild(para);
			      } else {
			        para.textContent = `File name ${file.name}: Not a valid file type. Update your selection.`;
			        listItem.appendChild(para);
			      }

			      list.appendChild(listItem);
			    }
			  }
			}
</script>

</body>
</html>
