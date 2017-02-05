<?xml version="1.0"?>
<!DOCTYPE html [
        <!ENTITY nbsp "&#160;">

]>
<html>

<head>

    <style>
        @font-face {
            font-family: DejaVu Sans;
            src: url(fonts/DejaVuSans.ttf);
            -fs-pdf-font-embed: embed;
            -fs-pdf-font-encoding: Identity-H;
        }

        body {
            font-family: DejaVu Sans;
        }

        h1 {
            font-size: 24px;
            margin-bottom: 1rem;
            margin-top: 1rem;

        }

        h2 {
            font-size: 20px;
            margin-bottom: 1rem;
            margin-top: 1rem;
        }

        h3 {
            font-size: 16px;
            margin-bottom: 1rem;
            margin-top: 1rem;
        }

        body {
            counter-reset: h1
        }

        h1 {
            counter-reset: h2
        }

        h2 {
            counter-reset: h3
        }

        h3 {
            counter-reset: h4
        }

        h4 {
            counter-reset: h5
        }

        h5 {
            counter-reset: h6
        }

        h1:before {
            counter-increment: h1;
            content: counter(h1) ". "
        }

        h2:before {
            counter-increment: h2;
            content: counter(h1) "." counter(h2) ". "
        }

        h3:before {
            counter-increment: h3;
            content: counter(h1) "." counter(h2) "." counter(h3) ". "
        }

        h4:before {
            counter-increment: h4;
            content: counter(h1) "." counter(h2) "." counter(h3) "." counter(h4) ". "
        }

        h5:before {
            counter-increment: h5;
            content: counter(h1) "." counter(h2) "." counter(h3) "." counter(h4) "." counter(h5) ". "
        }

        h6:before {
            counter-increment: h6;
            content: counter(h1) "." counter(h2) "." counter(h3) "." counter(h4) "." counter(h5) "." counter(h6) ". "
        }

        h1.nocount:before,h2.nocount:before, h3.nocount:before, h4.nocount:before, h5.nocount:before, h6.nocount:before {
            content: "";
            counter-increment: none
        }

        div.header {
            display: block;
            text-align: center;
            position: running(header);
        }

        div.footer {
            display: block;
            text-align: center;
            position: running(footer);
        }

        div.content {
            page-break-after: always;
        }

        @page {
            @top-center {
                content: element(header)
            }
        }

        @page {
            @bottom-center {
                content: element(footer)
            }
        }

        .toc a::after {
            content: leader('.') target-counter(attr(href), page);
        }

        .toc ul {
            list-style: none;
        }

    </style>


</head>

<body>

${content}

</body>

</html>